package pavball.hr.whitenoise.domain.usecase.sleep

import pavball.hr.whitenoise.repositories.SleepRepository

internal interface RateSleepSessionUseCase {
    suspend operator fun invoke(id: Long, quality: Int)
}

internal class RateSleepSession(private val repository: SleepRepository) : RateSleepSessionUseCase {
    override suspend fun invoke(id: Long, quality: Int) = repository.rateSession(id, quality.coerceIn(1, 5))
}
