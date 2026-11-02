package pavball.hr.whitenoise.domain.usecase.mix

import pavball.hr.whitenoise.repositories.SoundMixRepository

internal interface SaveSoundMixUseCase {
    suspend operator fun invoke(
        name: String,
        leadSoundId: String,
        leadVolume: Float,
        layers: Map<String, Float>,
        createdAt: Long
    )
}

internal class SaveSoundMix(private val repository: SoundMixRepository) : SaveSoundMixUseCase {
    override suspend fun invoke(
        name: String,
        leadSoundId: String,
        leadVolume: Float,
        layers: Map<String, Float>,
        createdAt: Long
    ) = repository.insertSoundMix(name, leadSoundId, leadVolume, layers, createdAt)
}
