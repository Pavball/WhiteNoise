package pavball.hr.whitenoise.domain.usecase.sleep

import pavball.hr.whitenoise.repositories.SleepRepository

internal interface StartSleepSessionUseCase {
    suspend operator fun invoke(startedAt: Long, alarmAt: Long?)
}

internal class StartSleepSession(private val repository: SleepRepository) : StartSleepSessionUseCase {
    override suspend fun invoke(startedAt: Long, alarmAt: Long?) = repository.startSession(startedAt, alarmAt)
}
