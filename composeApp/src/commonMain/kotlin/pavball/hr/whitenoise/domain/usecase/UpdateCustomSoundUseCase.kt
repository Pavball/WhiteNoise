package pavball.hr.whitenoise.domain.usecase

import pavball.hr.whitenoise.repositories.CustomSoundRepository

internal interface UpdateCustomSoundUseCase {

    suspend operator fun invoke(id: String, displayName: String)
}

internal class UpdateCustomSound(private val customSoundRepository: CustomSoundRepository) :
    UpdateCustomSoundUseCase {
    override suspend fun invoke(id: String, displayName: String) = customSoundRepository.updateCustomSound(id, displayName)

}