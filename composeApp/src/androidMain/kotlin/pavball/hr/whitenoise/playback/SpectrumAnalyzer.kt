package pavball.hr.whitenoise.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val BAND_COUNT = MainScreenViewModel.SPECTRUM_BANDS
private const val FFT_SIZE = 1024
private const val MIN_HZ = 50f
private const val MAX_HZ = 16_000f

/** Log-spaced band edges, so every octave gets about the same number of bars. */
private val BAND_EDGES_HZ = FloatArray(BAND_COUNT + 1) { MIN_HZ * (MAX_HZ / MIN_HZ).pow(it.toFloat() / BAND_COUNT) }

/**
 * Taps the decoded audio of one player and keeps the power of each frequency
 * band of the latest [FFT_SIZE]-sample frame. It runs on that player's playback
 * thread; readers on other threads see whole frames because [bands] is
 * replaced, never written in place.
 */
@OptIn(UnstableApi::class)
internal class SpectrumAnalyzer : TeeAudioProcessor.AudioBufferSink {

    /** The player's volume. It is applied after this tap, in the AudioTrack. */
    @Volatile
    var gain = 1f

    @Volatile
    private var bands = FloatArray(BAND_COUNT)

    @Volatile
    private var updatedAtNanos = 0L

    private var channelCount = 0
    private var encoding = C.ENCODING_INVALID
    private var bandBins = IntArray(BAND_COUNT + 1)

    private val frame = FloatArray(FFT_SIZE)
    private var filled = 0
    private val re = FloatArray(FFT_SIZE)
    private val im = FloatArray(FFT_SIZE)

    override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
        this.channelCount = channelCount
        this.encoding = encoding
        filled = 0
        val bins = IntArray(BAND_COUNT + 1)
        for (i in bins.indices) {
            val bin = (BAND_EDGES_HZ[i] * FFT_SIZE / sampleRateHz).toInt().coerceIn(1, FFT_SIZE / 2)
            // the lowest bands are narrower than one bin; give each at least one of its own
            bins[i] = if (i == 0) bin else max(bin, bins[i - 1] + 1).coerceAtMost(FFT_SIZE / 2)
        }
        bandBins = bins
    }

    override fun handleBuffer(buffer: ByteBuffer) {
        val channels = channelCount
        if (channels <= 0) return
        // The tee hands over a read-only view in big-endian order; PCM is native order.
        val data = buffer.order(ByteOrder.nativeOrder())
        when (encoding) {
            C.ENCODING_PCM_16BIT -> while (data.remaining() >= 2 * channels) {
                var sum = 0f
                repeat(channels) { sum += data.short / 32768f }
                push(sum / channels)
            }

            C.ENCODING_PCM_FLOAT -> while (data.remaining() >= 4 * channels) {
                var sum = 0f
                repeat(channels) { sum += data.float }
                push(sum / channels)
            }
        }
    }

    /** The latest band powers, or null when nothing was analysed recently (paused, stopped). */
    fun latest(nowNanos: Long): FloatArray? =
        if (nowNanos - updatedAtNanos < STALE_NANOS) bands else null

    private fun push(sample: Float) {
        frame[filled++] = sample
        if (filled == FFT_SIZE) {
            filled = 0
            analyze()
        }
    }

    private fun analyze() {
        for (i in 0 until FFT_SIZE) {
            re[i] = frame[i] * WINDOW[i]
            im[i] = 0f
        }
        fft(re, im)
        val result = FloatArray(BAND_COUNT)
        for (band in 0 until BAND_COUNT) {
            val from = bandBins[band]
            val to = max(bandBins[band + 1], from + 1)
            var sum = 0f
            for (k in from until to) sum += re[k] * re[k] + im[k] * im[k]
            result[band] = sum / (to - from) / WINDOW_POWER
        }
        bands = result
        updatedAtNanos = System.nanoTime()
    }

    private companion object {
        const val STALE_NANOS = 200_000_000L

        val WINDOW = FloatArray(FFT_SIZE) { (0.5 - 0.5 * cos(2 * PI * it / (FFT_SIZE - 1))).toFloat() }
        val WINDOW_POWER = WINDOW.sumOf { (it * it).toDouble() }.toFloat()
        val COS = FloatArray(FFT_SIZE / 2) { cos(2 * PI * it / FFT_SIZE).toFloat() }
        val SIN = FloatArray(FFT_SIZE / 2) { sin(2 * PI * it / FFT_SIZE).toFloat() }

        /** In-place iterative radix-2 FFT. */
        fun fft(re: FloatArray, im: FloatArray) {
            val n = re.size
            var j = 0
            for (i in 1 until n) {
                var bit = n shr 1
                while (j and bit != 0) {
                    j = j xor bit
                    bit = bit shr 1
                }
                j = j xor bit
                if (i < j) {
                    val tr = re[i]; re[i] = re[j]; re[j] = tr
                    val ti = im[i]; im[i] = im[j]; im[j] = ti
                }
            }
            var length = 2
            while (length <= n) {
                val half = length / 2
                val step = n / length
                for (start in 0 until n step length) {
                    for (k in 0 until half) {
                        val wr = COS[k * step]
                        val wi = -SIN[k * step]
                        val a = start + k
                        val b = a + half
                        val tr = re[b] * wr - im[b] * wi
                        val ti = re[b] * wi + im[b] * wr
                        re[b] = re[a] - tr
                        im[b] = im[a] - ti
                        re[a] += tr
                        im[a] += ti
                    }
                }
                length = length shl 1
            }
        }
    }
}

/** Builds the players' audio output with [analyzer] tapping the decoded audio. */
@OptIn(UnstableApi::class)
internal class AnalyzingRenderersFactory(
    context: Context,
    private val analyzer: SpectrumAnalyzer
) : DefaultRenderersFactory(context) {
    override fun buildAudioSink(
        context: Context,
        enableFloatOutput: Boolean,
        enableAudioTrackPlaybackParams: Boolean
    ): AudioSink = DefaultAudioSink.Builder(context)
        .setAudioProcessors(arrayOf(TeeAudioProcessor(analyzer)))
        .setEnableFloatOutput(enableFloatOutput)
        .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
        .build()
}

/**
 * Combines the analyzers of every playing player (the main sound and its
 * layers) into bar levels for the visualizer. The service and the UI share a
 * process, so the UI reads this directly instead of going through the session.
 */
internal object PlaybackSpectrum {

    private val analyzers = CopyOnWriteArrayList<SpectrumAnalyzer>()

    fun register(analyzer: SpectrumAnalyzer) {
        analyzers.addIfAbsent(analyzer)
    }

    fun unregister(analyzer: SpectrumAnalyzer) {
        analyzers.remove(analyzer)
    }

    /**
     * Bar levels (0..1). The loudest band of the content sits near the top, so quiet
     * and loud sounds both fill the visualizer, while volume and the sleep-timer fade
     * still shrink the bars.
     */
    fun levels(): Flow<FloatArray> = flow {
        val shown = FloatArray(BAND_COUNT)
        val raw = FloatArray(BAND_COUNT)
        val heard = FloatArray(BAND_COUNT)
        var reference = REFERENCE_FLOOR_DB
        var lastNanos = System.nanoTime()
        var silentEmitted = false
        while (true) {
            val now = System.nanoTime()
            val seconds = (now - lastNanos) / 1e9f
            lastNanos = now

            raw.fill(0f)
            heard.fill(0f)
            var active = false
            for (analyzer in analyzers) {
                val bands = analyzer.latest(now) ?: continue
                val power = analyzer.gain * analyzer.gain
                active = true
                for (i in 0 until BAND_COUNT) {
                    raw[i] += bands[i]
                    heard[i] += bands[i] * power
                }
            }

            var loudest = Float.NEGATIVE_INFINITY
            if (active) for (i in 0 until BAND_COUNT) loudest = max(loudest, decibels(raw[i]) + TILT_DB[i])
            reference = maxOf(reference - REFERENCE_DECAY_DB_PER_S * seconds, loudest, REFERENCE_FLOOR_DB)

            for (i in 0 until BAND_COUNT) {
                val target = if (active) {
                    ((decibels(heard[i]) + TILT_DB[i] - reference + RANGE_DB) / RANGE_DB).coerceIn(0f, 1f)
                } else 0f
                val speed = if (target > shown[i]) ATTACK else RELEASE
                shown[i] += (target - shown[i]) * speed
            }
            // Once the bars have settled at zero, don't wake the UI again until sound returns.
            val silent = shown.all { it < 0.001f }
            if (!silent || !silentEmitted) emit(shown.copyOf())
            silentEmitted = silent
            delay(FRAME_MS)
        }
    }.flowOn(Dispatchers.Default)

    private fun decibels(power: Float) = 10f * log10(power + 1e-12f)

    private const val FRAME_MS = 16L
    private const val RANGE_DB = 24f // nature sounds keep most bands within a few dB of each other
    private const val REFERENCE_FLOOR_DB = -50f
    private const val REFERENCE_DECAY_DB_PER_S = 3f
    private const val ATTACK = 0.45f
    private const val RELEASE = 0.12f

    /** +3 dB per octave around 1 kHz, so the high bands don't look empty next to the bass. */
    private val TILT_DB = FloatArray(BAND_COUNT) {
        val centerHz = sqrt(BAND_EDGES_HZ[it] * BAND_EDGES_HZ[it + 1])
        3f * log2(centerHz / 1000f)
    }
}
