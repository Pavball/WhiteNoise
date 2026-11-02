package pavball.hr.whitenoise.domain.usecase.mix

import pavball.hr.whitenoise.repositories.SoundMixRepository

internal interface DeleteSoundMixUseCase {
    suspend operator fun invoke(id: Long)
}

internal class DeleteSoundMix(private val repository: SoundMixRepository) : DeleteSoundMixUseCase {
    override suspend fun invoke(id: Long) = repository.deleteSoundMix(id)
}
