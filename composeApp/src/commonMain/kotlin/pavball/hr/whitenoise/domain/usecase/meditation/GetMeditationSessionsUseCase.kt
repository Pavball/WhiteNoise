package pavball.hr.whitenoise.domain.usecase.meditation

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.MeditationSession
import pavball.hr.whitenoise.repositories.MeditationRepository

internal interface GetMeditationSessionsUseCase {
    operator fun invoke(): Flow<List<MeditationSession>>
}

internal class GetMeditationSessions(private val repository: MeditationRepository) : GetMeditationSessionsUseCase {
    override fun invoke(): Flow<List<MeditationSession>> = repository.getMeditationSessions()
}
