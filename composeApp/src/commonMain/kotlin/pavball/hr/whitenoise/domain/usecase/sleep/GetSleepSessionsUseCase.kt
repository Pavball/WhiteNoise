package pavball.hr.whitenoise.domain.usecase.sleep

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.SleepSession
import pavball.hr.whitenoise.repositories.SleepRepository

internal interface GetSleepSessionsUseCase {
    /** Completed nights, newest first. */
    operator fun invoke(): Flow<List<SleepSession>>
}

internal class GetSleepSessions(private val repository: SleepRepository) : GetSleepSessionsUseCase {
    override fun invoke(): Flow<List<SleepSession>> = repository.getCompletedSessions()
}
