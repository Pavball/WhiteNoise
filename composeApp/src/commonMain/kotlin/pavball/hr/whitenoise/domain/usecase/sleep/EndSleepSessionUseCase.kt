package pavball.hr.whitenoise.domain.usecase.sleep

import pavball.hr.whitenoise.repositories.SleepRepository

internal interface EndSleepSessionUseCase {
    suspend operator fun invoke(id: Long, endedAt: Long)
}

internal class EndSleepSession(private val repository: SleepRepository) : EndSleepSessionUseCase {
    override suspend fun invoke(id: Long, endedAt: Long) = repository.endSession(id, endedAt)
}
