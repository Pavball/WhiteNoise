package pavball.hr.whitenoise.domain.usecase

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.repositorites.CustomSoundRepository

internal interface UpdateCustomSoundUseCase {

    suspend operator fun invoke(id: String, displayName: String)
}

internal class UpdateCustomSound(private val customSoundRepository: CustomSoundRepository) :
    UpdateCustomSoundUseCase {
    override suspend fun invoke(id: String, displayName: String) = customSoundRepository.updateCustomSound(id, displayName)

}