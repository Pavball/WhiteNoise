package pavball.hr.whitenoise.domain.usecase.sleep

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.SleepSession
import pavball.hr.whitenoise.repositories.SleepRepository

internal interface GetActiveSleepSessionUseCase {
    operator fun invoke(): Flow<SleepSession?>
}

internal class GetActiveSleepSession(private val repository: SleepRepository) : GetActiveSleepSessionUseCase {
    override fun invoke(): Flow<SleepSession?> = repository.getActiveSession()
}
