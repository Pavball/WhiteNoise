package pavball.hr.whitenoise.domain.usecase

import pavball.hr.whitenoise.repositories.CustomSoundRepository

internal interface DeleteCustomSoundUseCase {

    suspend operator fun invoke(id: String)
}

internal class DeleteCustomSound(private val customSoundRepository: CustomSoundRepository) :
    DeleteCustomSoundUseCase {
    override suspend fun invoke(id: String) = customSoundRepository.deleteCustomSound(id)

}