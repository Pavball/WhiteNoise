package pavball.hr.whitenoise.playback

import android.os.Bundle
import android.os.SystemClock
import androidx.media3.session.SessionCommand

/**
 * Custom commands the app sends to [PlaybackService] and the session extras
 * the service publishes back, describing the sleep timer.
 */
internal object PlaybackContract {

    const val CMD_PLAY_SOUND = "pavball.hr.whitenoise.PLAY_SOUND"
    const val CMD_START_TIMER = "pavball.hr.whitenoise.START_TIMER"
    const val CMD_EXTEND_TIMER = "pavball.hr.whitenoise.EXTEND_TIMER"
    const val CMD_CANCEL_TIMER = "pavball.hr.whitenoise.CANCEL_TIMER"
    const val CMD_SET_FADE = "pavball.hr.whitenoise.SET_FADE"

    val ALL_COMMANDS = listOf(
        CMD_PLAY_SOUND, CMD_START_TIMER, CMD_EXTEND_TIMER, CMD_CANCEL_TIMER, CMD_SET_FADE
    )

    // command arguments
    const val ARG_SOUND_ID = "sound_id"
    const val ARG_MINUTES = "minutes"
    const val ARG_FADE_ENABLED = "fade_enabled"
    const val ARG_FADE_SECONDS = "fade_seconds"

    // session extras
    private const val EXTRA_TOTAL_MS = "timer_total_ms"
    private const val EXTRA_REMAINING_MS = "timer_remaining_ms"
    private const val EXTRA_RUNNING = "timer_running"
    private const val EXTRA_SNAPSHOT_ELAPSED = "timer_snapshot_elapsed"
    private const val EXTRA_FADE_STARTED = "timer_fade_started"

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

        fun toBundle() = Bundle().apply {
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
}
