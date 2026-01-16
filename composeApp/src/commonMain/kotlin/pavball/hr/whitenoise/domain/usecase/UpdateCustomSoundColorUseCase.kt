package pavball.hr.whitenoise.domain.usecase

import pavball.hr.whitenoise.repositories.CustomSoundRepository

internal interface UpdateCustomSoundColorUseCase {

    suspend operator fun invoke(id: String, colorId: String)
}

internal class UpdateCustomSoundColor(private val customSoundRepository: CustomSoundRepository) :
    UpdateCustomSoundColorUseCase {
    override suspend fun invoke(id: String, colorId: String) = customSoundRepository.updateCustomSoundColor(id, colorId)

}