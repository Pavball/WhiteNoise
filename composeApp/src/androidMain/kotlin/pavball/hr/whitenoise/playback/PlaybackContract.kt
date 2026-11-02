package pavball.hr.whitenoise.playback

import android.os.Bundle
import android.os.SystemClock
import androidx.media3.session.SessionCommand

/**
 * Custom commands the app sends to [PlaybackService] and the session extras
 * the service publishes back, describing the sleep timer and the sound mix.
 */
internal object PlaybackContract {

    const val CMD_PLAY_SOUND = "pavball.hr.whitenoise.PLAY_SOUND"
    const val CMD_START_TIMER = "pavball.hr.whitenoise.START_TIMER"
    const val CMD_EXTEND_TIMER = "pavball.hr.whitenoise.EXTEND_TIMER"
    const val CMD_CANCEL_TIMER = "pavball.hr.whitenoise.CANCEL_TIMER"
    const val CMD_SET_FADE = "pavball.hr.whitenoise.SET_FADE"
    const val CMD_SET_LEAD_VOLUME = "pavball.hr.whitenoise.SET_LEAD_VOLUME"
    const val CMD_SET_LAYER = "pavball.hr.whitenoise.SET_LAYER"
    const val CMD_CLEAR_LAYERS = "pavball.hr.whitenoise.CLEAR_LAYERS"
    const val CMD_PLAY_MIX = "pavball.hr.whitenoise.PLAY_MIX"

    val ALL_COMMANDS = listOf(
        CMD_PLAY_SOUND, CMD_START_TIMER, CMD_EXTEND_TIMER, CMD_CANCEL_TIMER, CMD_SET_FADE,
        CMD_SET_LEAD_VOLUME, CMD_SET_LAYER, CMD_CLEAR_LAYERS, CMD_PLAY_MIX
    )

    // command arguments
    const val ARG_SOUND_ID = "sound_id"
    const val ARG_MINUTES = "minutes"
    const val ARG_FADE_ENABLED = "fade_enabled"
    const val ARG_FADE_SECONDS = "fade_seconds"
    const val ARG_VOLUME = "volume"
    const val ARG_LAYER_IDS = "layer_ids"
    const val ARG_LAYER_VOLUMES = "layer_volumes"

    // session extras
    private const val EXTRA_TOTAL_MS = "timer_total_ms"
    private const val EXTRA_REMAINING_MS = "timer_remaining_ms"
    private const val EXTRA_RUNNING = "timer_running"
    private const val EXTRA_SNAPSHOT_ELAPSED = "timer_snapshot_elapsed"
    private const val EXTRA_FADE_STARTED = "timer_fade_started"
    private const val EXTRA_LEAD_VOLUME = "mix_lead_volume"
    private const val EXTRA_LAYER_IDS = "mix_layer_ids"
    private const val EXTRA_LAYER_VOLUMES = "mix_layer_volumes"

    fun command(action: String) = SessionCommand(action, Bundle.EMPTY)

    /** Timer state at one moment; [remainingMsNow] extrapolates it while running. */
    data class TimerSnapshot(
        val totalMs: Long,
        val remainingMs: Long,
        val isRunning: Boolean,
        val snapshotElapsedRealtime: Long,
        val fadeStarted: Boolean
    ) {
        fun remainingMsNow(): Long =
            if (isRunning) {
                (remainingMs - (SystemClock.elapsedRealtime() - snapshotElapsedRealtime))
                    .coerceAtLeast(0)
            } else remainingMs

        fun writeTo(extras: Bundle) = extras.apply {
            putLong(EXTRA_TOTAL_MS, totalMs)
            putLong(EXTRA_REMAINING_MS, remainingMs)
            putBoolean(EXTRA_RUNNING, isRunning)
            putLong(EXTRA_SNAPSHOT_ELAPSED, snapshotElapsedRealtime)
            putBoolean(EXTRA_FADE_STARTED, fadeStarted)
        }

        companion object {
            /** Returns null when the bundle describes no active timer. */
            fun fromBundle(extras: Bundle): TimerSnapshot? {
                val total = extras.getLong(EXTRA_TOTAL_MS, 0L)
                if (total <= 0L) return null
                return TimerSnapshot(
                    totalMs = total,
                    remainingMs = extras.getLong(EXTRA_REMAINING_MS, 0L),
                    isRunning = extras.getBoolean(EXTRA_RUNNING, false),
                    snapshotElapsedRealtime = extras.getLong(EXTRA_SNAPSHOT_ELAPSED, 0L),
                    fadeStarted = extras.getBoolean(EXTRA_FADE_STARTED, false)
                )
            }
        }
    }

    /** The main sound's volume and the sounds layered on top of it, by id. */
    data class MixSnapshot(
        val leadVolume: Float,
        val layers: Map<String, Float>
    ) {
        fun writeTo(extras: Bundle) = extras.apply {
            putFloat(EXTRA_LEAD_VOLUME, leadVolume)
            putStringArray(EXTRA_LAYER_IDS, layers.keys.toTypedArray())
            putFloatArray(EXTRA_LAYER_VOLUMES, layers.values.toFloatArray())
        }

        companion object {
            fun fromBundle(extras: Bundle): MixSnapshot =
                MixSnapshot(
                    leadVolume = extras.getFloat(EXTRA_LEAD_VOLUME, 1f),
                    layers = layersFrom(extras, EXTRA_LAYER_IDS, EXTRA_LAYER_VOLUMES)
                )

            /** Reads the layers of a [CMD_PLAY_MIX] command. */
            fun layersFromArgs(args: Bundle): Map<String, Float> =
                layersFrom(args, ARG_LAYER_IDS, ARG_LAYER_VOLUMES)

            private fun layersFrom(bundle: Bundle, idsKey: String, volumesKey: String): Map<String, Float> {
                val ids = bundle.getStringArray(idsKey) ?: return emptyMap()
                val volumes = bundle.getFloatArray(volumesKey) ?: return emptyMap()
                return ids.zip(volumes.toList()).toMap()
            }
        }
    }
}
