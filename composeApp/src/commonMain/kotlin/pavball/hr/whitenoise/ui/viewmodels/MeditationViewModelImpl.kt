package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import pavball.hr.whitenoise.domain.model.Mood
import pavball.hr.whitenoise.domain.usecase.meditation.SaveMeditationSessionUseCase
import pavball.hr.whitenoise.ui.components.utils.nowEpochMillis
import kotlin.time.TimeSource

internal class MeditationViewModelImpl(
    private val saveMeditationSessionUseCase: SaveMeditationSessionUseCase
) : MeditationViewModel() {

    private var tickerJob: Job? = null
    private var runStart: TimeSource.Monotonic.ValueTimeMark? = null
    private var elapsedBeforeRunMs = 0L // time meditated before the latest pause

    private val totalMs get() = getCurrentState().durationMinutes * 60_000L

    override fun selectMood(mood: Mood) {
        if (getCurrentState().phase != MeditationPhase.CHOOSING) return
        runCommand { updateState { copy(mood = mood) } }
    }

    override fun selectDuration(minutes: Int) {
        if (getCurrentState().phase != MeditationPhase.CHOOSING) return
        runCommand { updateState { copy(durationMinutes = minutes, remainingMs = minutes * 60_000L) } }
    }

    override fun start() {
        val state = getCurrentState()
        if (state.phase != MeditationPhase.CHOOSING) return
        runCommand { updateState { copy(mood = mood ?: Mood.HAPPY) } }
        elapsedBeforeRunMs = 0L
        run()
    }

    override fun pause() {
        if (getCurrentState().phase != MeditationPhase.RUNNING) return
        elapsedBeforeRunMs = elapsedMs()
        runStart = null
        tickerJob?.cancel()
        runCommand { updateState { copy(phase = MeditationPhase.PAUSED) } }
    }

    override fun resume() {
        if (getCurrentState().phase != MeditationPhase.PAUSED) return
        run()
    }

    override fun finish() {
        val phase = getCurrentState().phase
        if (phase != MeditationPhase.RUNNING && phase != MeditationPhase.PAUSED) return
        complete(elapsedMs())
    }

    override fun reset() {
        tickerJob?.cancel()
        runStart = null
        elapsedBeforeRunMs = 0L
        runCommand {
            updateState {
                copy(
                    phase = MeditationPhase.CHOOSING,
                    remainingMs = durationMinutes * 60_000L,
                    breathStep = null,
                    breathStepProgress = 0f,
                    breathScale = 1f
                )
            }
        }
    }

    private fun run() {
        runStart = TimeSource.Monotonic.markNow()
        tickerJob?.cancel()
        tickerJob = runCommand {
            updateState { copy(phase = MeditationPhase.RUNNING) }
            while (isActive) {
                val elapsed = elapsedMs()
                if (elapsed >= totalMs) {
                    complete(totalMs)
                    return@runCommand
                }
                render(elapsed)
                delay(TICK_MS)
            }
        }
    }

    private suspend fun render(elapsed: Long) {
        val pattern = BreathingPattern.forMood(getCurrentState().mood ?: Mood.HAPPY)
        var inCycle = elapsed % pattern.cycleMs
        var stepIndex = 0
        while (stepIndex < pattern.steps.lastIndex && inCycle >= pattern.steps[stepIndex].seconds * 1000L) {
            inCycle -= pattern.steps[stepIndex].seconds * 1000L
            stepIndex++
        }
        val step = pattern.steps[stepIndex]
        val progress = (inCycle / (step.seconds * 1000f)).coerceIn(0f, 1f)
        val scale = when (step.action) {
            BreathAction.INHALE -> SMALL + (LARGE - SMALL) * progress
            BreathAction.EXHALE -> LARGE - (LARGE - SMALL) * progress
            // a hold keeps the size the previous step ended on
            BreathAction.HOLD ->
                if (pattern.steps.getOrNull(stepIndex - 1)?.action == BreathAction.EXHALE) SMALL else LARGE
        }
        updateState {
            copy(
                remainingMs = (totalMs - elapsed).coerceAtLeast(0),
                breathStep = step,
                breathStepProgress = progress,
                breathScale = scale
            )
        }
    }

    private fun complete(meditatedMs: Long) {
        tickerJob?.cancel()
        runStart = null
        val mood = getCurrentState().mood ?: Mood.HAPPY
        runCommand {
            if (meditatedMs >= MIN_SAVED_MS) {
                saveMeditationSessionUseCase(mood, nowEpochMillis(), meditatedMs)
            }
            updateState {
                copy(phase = MeditationPhase.FINISHED, remainingMs = 0, breathStep = null, breathScale = 1f)
            }
        }
    }

    private fun elapsedMs(): Long =
        elapsedBeforeRunMs + (runStart?.elapsedNow()?.inWholeMilliseconds ?: 0L)

    private companion object {
        const val TICK_MS = 100L
        const val MIN_SAVED_MS = 60_000L
        const val SMALL = 0.8f
        const val LARGE = 1.1f
    }
}
