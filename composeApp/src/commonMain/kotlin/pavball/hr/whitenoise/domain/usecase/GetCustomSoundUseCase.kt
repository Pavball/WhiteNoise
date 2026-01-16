package pavball.hr.whitenoise.domain.usecase

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.repositories.CustomSoundRepository

internal interface GetCustomSoundUseCase {

    operator fun invoke(): Flow<List<CustomSound>>
}

internal class GetCustomSound(private val customSoundRepository: CustomSoundRepository) :
    GetCustomSoundUseCase {
    override fun invoke(): Flow<List<CustomSound>> = customSoundRepository.getCustomSounds()

}