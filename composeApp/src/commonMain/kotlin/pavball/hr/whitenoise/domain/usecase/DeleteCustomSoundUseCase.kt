package pavball.hr.whitenoise.domain.usecase

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.repositorites.CustomSoundRepository

internal interface DeleteCustomSoundUseCase {

    suspend operator fun invoke(id: String)
}

internal class DeleteCustomSound(private val customSoundRepository: CustomSoundRepository) :
    DeleteCustomSoundUseCase {
    override suspend fun invoke(id: String) = customSoundRepository.deleteCustomSound(id)

}