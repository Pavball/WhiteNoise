package pavball.hr.whitenoise.domain.usecase.meditation

import pavball.hr.whitenoise.domain.model.Mood
import pavball.hr.whitenoise.repositories.MeditationRepository

internal interface SaveMeditationSessionUseCase {
    suspend operator fun invoke(mood: Mood, completedAt: Long, durationMs: Long)
}

internal class SaveMeditationSession(private val repository: MeditationRepository) : SaveMeditationSessionUseCase {
    override suspend fun invoke(mood: Mood, completedAt: Long, durationMs: Long) =
        repository.insertMeditationSession(mood, completedAt, durationMs)
}
