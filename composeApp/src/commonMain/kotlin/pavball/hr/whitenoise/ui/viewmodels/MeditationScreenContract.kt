package pavball.hr.whitenoise.ui.viewmodels

import pavball.hr.whitenoise.domain.model.Mood

enum class BreathAction { INHALE, HOLD, EXHALE }

data class BreathStep(val action: BreathAction, val seconds: Int)

/** A guided breathing rhythm matched to how the user feels. */
enum class BreathingPattern(val steps: List<BreathStep>) {
    // 4-7-8: long exhales calm the nervous system
    RELAX(listOf(BreathStep(BreathAction.INHALE, 4), BreathStep(BreathAction.HOLD, 7), BreathStep(BreathAction.EXHALE, 8))),
    // box breathing: steady and grounding
    BOX(listOf(BreathStep(BreathAction.INHALE, 4), BreathStep(BreathAction.HOLD, 4), BreathStep(BreathAction.EXHALE, 4), BreathStep(BreathAction.HOLD, 4))),
    // coherent breathing: even and light
    EVEN(listOf(BreathStep(BreathAction.INHALE, 5), BreathStep(BreathAction.EXHALE, 5)));

    val cycleMs: Long get() = steps.sumOf { it.seconds } * 1000L

    companion object {
        fun forMood(mood: Mood) = when (mood) {
            Mood.ANXIETY -> RELAX
            Mood.SAD -> BOX
            Mood.HAPPY -> EVEN
        }
    }
}

enum class MeditationPhase { CHOOSING, RUNNING, PAUSED, FINISHED }

data class MeditationViewState(
    val mood: Mood? = null,
    val durationMinutes: Int = 5,
    val durationOptions: List<Int> = listOf(3, 5, 10, 15),
    val phase: MeditationPhase = MeditationPhase.CHOOSING,
    val remainingMs: Long = 5 * 60_000L,
    val breathStep: BreathStep? = null,
    /** 0..1 progress through [breathStep]. */
    val breathStepProgress: Float = 0f,
    /** Size of the glowing circle: grows while inhaling, shrinks while exhaling. */
    val breathScale: Float = 1f
)

internal abstract class MeditationViewModel : BaseViewModel<MeditationViewState>(MeditationViewState()) {
    abstract fun selectMood(mood: Mood)
    abstract fun selectDuration(minutes: Int)
    abstract fun start()
    abstract fun pause()
    abstract fun resume()
    /** Ends early; keeps the session if at least a minute was done. */
    abstract fun finish()
    abstract fun reset()
}
