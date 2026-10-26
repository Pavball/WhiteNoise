package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.datetime.DayOfWeek
import pavball.hr.whitenoise.domain.model.SleepSession

// --------------------------------------------------------------------
// ||                 SLEEP                                          ||
// --------------------------------------------------------------------
data class SleepViewState(
    val activeSession: SleepSession? = null,
    val wakeHour: Int = 7,
    val wakeMinute: Int = 0,
    /** While asleep: time since bedtime. Otherwise: time until the chosen wake-up time. */
    val clockMs: Long = 0,
    /** Set after waking up, so the screen can ask how the night was. */
    val sessionToRate: SleepSession? = null,
    /** False when no alarm app accepted the wake-up alarm. */
    val alarmScheduled: Boolean? = null
) {
    val isSleeping: Boolean get() = activeSession != null
}

internal abstract class SleepViewModel : BaseViewModel<SleepViewState>(SleepViewState()) {
    abstract fun updateWakeTime(hour: Int, minute: Int)
    abstract fun startSleep(alarmLabel: String)
    abstract fun wakeUp()
    abstract fun rateNight(quality: Int)
    abstract fun skipRating()
    abstract fun cancelSleep()
}

// --------------------------------------------------------------------
// ||                 TRACKING                                       ||
// --------------------------------------------------------------------
data class TrackedDay(
    val dayOfWeek: DayOfWeek,
    val sleepMs: Long?     // null when no night ended on that day
)

data class TrackingViewState(
    val week: List<TrackedDay> = emptyList(),   // oldest first, ending today
    val lastNight: SleepSession? = null,
    val totalNights: Int = 0,
    val averageSleepMs: Long? = null,
    val averageQuality: Float? = null,          // 1..5
    val latestNote: String? = null,
    val meditationMinutesThisWeek: Int = 0,
    val isLoaded: Boolean = false
)

internal abstract class TrackingViewModel : BaseViewModel<TrackingViewState>(TrackingViewState())
