package pavball.hr.whitenoise.playback

import android.content.Context
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Generates the white, pink and brown noise sounds as short seamless WAV loops
 * on first use, so they don't add to the APK size.
 */
internal object NoiseGenerator {

    private enum class NoiseColor(val key: String, val targetRms: Float) {
        // Brighter noise sounds louder at the same level, so it is generated quieter.
        WHITE("white_noise", 0.06f),
        PINK("pink_noise", 0.09f),
        BROWN("brown_noise", 0.13f)
    }

    private const val SAMPLE_RATE = 44_100
    private const val LOOP_SECONDS = 30
    private const val CROSSFADE_SECONDS = 2
    private const val VERSION = 1 // bump to regenerate after changing the algorithm

    /** Writes any missing loops and returns them by sound key. Blocking: call off the main thread. */
    fun ensureLoops(context: Context): Map<String, File> {
        val dir = File(context.filesDir, "noise").apply { mkdirs() }
        return NoiseColor.entries.associate { color ->
            val file = File(dir, "${color.key}_v$VERSION.wav")
            if (!file.exists()) writeWav(file, generate(color))
            color.key to file
        }
    }

    private fun generate(color: NoiseColor): FloatArray {
        val loop = SAMPLE_RATE * LOOP_SECONDS
        val fade = SAMPLE_RATE * CROSSFADE_SECONDS
        val random = Random(color.ordinal + 1)
        val raw = FloatArray(loop + fade)

        when (color) {
            NoiseColor.WHITE -> for (i in raw.indices) raw[i] = random.nextFloat() * 2f - 1f

            NoiseColor.PINK -> {
                // Paul Kellet's filter: white noise shaped to -3 dB per octave.
                var b0 = 0f; var b1 = 0f; var b2 = 0f; var b3 = 0f; var b4 = 0f; var b5 = 0f; var b6 = 0f
                for (i in raw.indices) {
                    val white = random.nextFloat() * 2f - 1f
                    b0 = 0.99886f * b0 + white * 0.0555179f
                    b1 = 0.99332f * b1 + white * 0.0750759f
                    b2 = 0.96900f * b2 + white * 0.1538520f
                    b3 = 0.86650f * b3 + white * 0.3104856f
                    b4 = 0.55000f * b4 + white * 0.5329522f
                    b5 = -0.7616f * b5 - white * 0.0168980f
                    raw[i] = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f
                    b6 = white * 0.115926f
                }
            }

            NoiseColor.BROWN -> {
                // Leaky integrator: -6 dB per octave without drifting off.
                var last = 0f
                for (i in raw.indices) {
                    last = (last + 0.02f * (random.nextFloat() * 2f - 1f)) / 1.02f
                    raw[i] = last
                }
            }
        }

        // Blend the extra tail into the start, so the end of the loop flows into
        // its beginning like the continuous signal it was cut from.
        val out = raw.copyOf(loop)
        for (i in 0 until fade) {
            val t = i.toFloat() / fade
            out[i] = raw[i] * sqrt(t) + raw[loop + i] * sqrt(1f - t) // equal power, the two are uncorrelated
        }
        normalize(out, color.targetRms)
        return out
    }

    private fun normalize(samples: FloatArray, targetRms: Float) {
        val mean = samples.average().toFloat()
        var sumSquares = 0.0
        for (i in samples.indices) {
            samples[i] -= mean
            sumSquares += samples[i] * samples[i]
        }
        val rms = sqrt(sumSquares / samples.size).toFloat()
        if (rms == 0f) return
        val gain = targetRms / rms
        for (i in samples.indices) samples[i] = (samples[i] * gain).coerceIn(-1f, 1f)
    }

    /** 16-bit mono PCM; written to a temp file first so a crash never leaves half a loop behind. */
    private fun writeWav(file: File, samples: FloatArray) {
        val dataBytes = samples.size * 2
        val buffer = ByteBuffer.allocate(44 + dataBytes).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray()); putInt(36 + dataBytes); put("WAVE".toByteArray())
            put("fmt ".toByteArray()); putInt(16)
            putShort(1)                      // PCM
            putShort(1)                      // mono
            putInt(SAMPLE_RATE)
            putInt(SAMPLE_RATE * 2)          // byte rate
            putShort(2)                      // block align
            putShort(16)                     // bits per sample
            put("data".toByteArray()); putInt(dataBytes)
            for (sample in samples) putShort((sample * Short.MAX_VALUE).roundToInt().toShort())
        }
        val temp = File(file.parentFile, "${file.name}.tmp")
        temp.writeBytes(buffer.array())
        if (!temp.renameTo(file)) temp.delete()
    }
}
