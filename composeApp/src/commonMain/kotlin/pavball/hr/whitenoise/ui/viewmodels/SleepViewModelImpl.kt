package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntriesUseCase
import pavball.hr.whitenoise.domain.usecase.meditation.GetMeditationSessionsUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.DeleteSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.EndSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.GetActiveSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.GetSleepSessionsUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.RateSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.StartSleepSessionUseCase
import pavball.hr.whitenoise.platform.WakeUpAlarm
import pavball.hr.whitenoise.ui.components.utils.nextOccurrenceOf
import pavball.hr.whitenoise.ui.components.utils.nowEpochMillis
import pavball.hr.whitenoise.ui.components.utils.plusDays
import pavball.hr.whitenoise.ui.components.utils.toLocalDateTime

internal class SleepViewModelImpl(
    private val getActiveSleepSessionUseCase: GetActiveSleepSessionUseCase,
    private val getSleepSessionsUseCase: GetSleepSessionsUseCase,
    private val startSleepSessionUseCase: StartSleepSessionUseCase,
    private val endSleepSessionUseCase: EndSleepSessionUseCase,
    private val rateSleepSessionUseCase: RateSleepSessionUseCase,
    private val deleteSleepSessionUseCase: DeleteSleepSessionUseCase,
    private val wakeUpAlarm: WakeUpAlarm
) : SleepViewModel() {

    // --------------------------------------------------------------------
    // ||                 INITIALIZE SECTION                             ||
    // --------------------------------------------------------------------
    init {
        // Default the wheel to the last night's wake-up time.
        runCommand {
            val lastAlarm = getSleepSessionsUseCase().first().firstOrNull()?.alarmAt
            if (lastAlarm != null) {
                val time = lastAlarm.toLocalDateTime()
                updateState { copy(wakeHour = time.hour, wakeMinute = time.minute) }
            }
        }
        runCommand {
            getActiveSleepSessionUseCase().collectLatest { session ->
                updateState { copy(activeSession = session) }
                session?.alarmAt?.let { alarm ->
                    val time = alarm.toLocalDateTime()
                    updateState { copy(wakeHour = time.hour, wakeMinute = time.minute) }
                }
                tickClock()
            }
        }
    }

    /** Refreshes [SleepViewState.clockMs] every second (collectLatest restarts it on change). */
    private suspend fun tickClock() {
        while (viewModelScope.isActive) {
            val state = getCurrentState()
            val now = nowEpochMillis()
            val session = state.activeSession
            val clock = if (session != null) now - session.startedAt
            else nextOccurrenceOf(state.wakeHour, state.wakeMinute, now) - now
            updateState { copy(clockMs = clock.coerceAtLeast(0)) }
            delay(1_000 - now % 1_000)
        }
    }

    // --------------------------------------------------------------------
    // ||                 SLEEP SECTION                                  ||
    // --------------------------------------------------------------------
    override fun updateWakeTime(hour: Int, minute: Int) {
        runCommand {
            updateState {
                val now = nowEpochMillis()
                copy(
                    wakeHour = hour,
                    wakeMinute = minute,
                    clockMs = if (activeSession == null) nextOccurrenceOf(hour, minute, now) - now else clockMs
                )
            }
        }
    }

    override fun startSleep(alarmLabel: String) {
        val state = getCurrentState()
        if (state.isSleeping) return
        val now = nowEpochMillis()
        val alarmAt = nextOccurrenceOf(state.wakeHour, state.wakeMinute, now)
        val scheduled = wakeUpAlarm.schedule(state.wakeHour, state.wakeMinute, alarmLabel)
        runCommand {
            updateState { copy(alarmScheduled = scheduled) }
            startSleepSessionUseCase(startedAt = now, alarmAt = alarmAt)
        }
    }

    override fun wakeUp() {
        val session = getCurrentState().activeSession ?: return
        runCommand {
            val now = nowEpochMillis()
            if (now - session.startedAt < MIN_NIGHT_MS) {
                // a false start (or a quick test) shouldn't count as a night
                deleteSleepSessionUseCase(session.id)
                updateState { copy(alarmScheduled = null) }
                return@runCommand
            }
            endSleepSessionUseCase(session.id, now)
            updateState { copy(sessionToRate = session.copy(endedAt = now), alarmScheduled = null) }
        }
    }

    override fun rateNight(quality: Int) {
        val session = getCurrentState().sessionToRate ?: return
        runCommand {
            rateSleepSessionUseCase(session.id, quality)
            updateState { copy(sessionToRate = null) }
        }
    }

    override fun skipRating() {
        runCommand { updateState { copy(sessionToRate = null) } }
    }

    override fun cancelSleep() {
        val session = getCurrentState().activeSession ?: return
        runCommand {
            deleteSleepSessionUseCase(session.id)
            updateState { copy(alarmScheduled = null) }
        }
    }

    private companion object {
        const val MIN_NIGHT_MS = 5 * 60_000L
    }
}

internal class TrackingViewModelImpl(
    getSleepSessionsUseCase: GetSleepSessionsUseCase,
    getJournalEntriesUseCase: GetJournalEntriesUseCase,
    getMeditationSessionsUseCase: GetMeditationSessionsUseCase
) : TrackingViewModel() {

    init {
        runCommand {
            combine(
                getSleepSessionsUseCase(),
                getJournalEntriesUseCase(),
                getMeditationSessionsUseCase()
            ) { nights, entries, meditations ->
                val today = nowEpochMillis().toLocalDateTime().date
                val weekStart = today.plusDays(-6)

                val week = (0..6).map { offset ->
                    val day = weekStart.plusDays(offset)
                    val sleptMs = nights
                        .filter { it.endedAt?.toLocalDateTime()?.date == day }
                        .sumOf { it.durationMs ?: 0L }
                    TrackedDay(day.dayOfWeek, sleptMs.takeIf { it > 0 })
                }
                val durations = nights.mapNotNull { it.durationMs }
                val qualities = nights.mapNotNull { it.quality }

                TrackingViewState(
                    week = week,
                    lastNight = nights.firstOrNull(),
                    totalNights = nights.size,
                    averageSleepMs = durations.takeIf { it.isNotEmpty() }?.average()?.toLong(),
                    averageQuality = qualities.takeIf { it.isNotEmpty() }?.average()?.toFloat(),
                    latestNote = entries.firstOrNull { it.type == JournalType.NOTE }?.text,
                    meditationMinutesThisWeek = meditations
                        .filter { it.completedAt.toLocalDateTime().date >= weekStart }
                        .sumOf { it.durationMs }
                        .let { (it / 60_000).toInt() },
                    isLoaded = true
                )
            }.collectLatest { emit(it) }
        }
    }
}
