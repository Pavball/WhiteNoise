package pavball.hr.whitenoise.domain.usecase.sleep

import pavball.hr.whitenoise.repositories.SleepRepository

internal interface DeleteSleepSessionUseCase {
    suspend operator fun invoke(id: Long)
}

internal class DeleteSleepSession(private val repository: SleepRepository) : DeleteSleepSessionUseCase {
    override suspend fun invoke(id: Long) = repository.deleteSession(id)
}
