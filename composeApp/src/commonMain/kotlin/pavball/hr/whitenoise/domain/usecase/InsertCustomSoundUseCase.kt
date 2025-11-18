package pavball.hr.whitenoise.domain.usecase


import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.repositorites.CustomSoundRepository

internal interface InsertCustomSoundUseCase {

    suspend operator fun invoke(
        id: String,
        displayName: String,
        uri: String,
        addedAt: Long
    )
}

internal class InsertCustomSound(private val customSoundRepository: CustomSoundRepository) :
    InsertCustomSoundUseCase {
    override suspend fun invoke(
        id: String,
        displayName: String,
        uri: String,
        addedAt: Long
    ) =
        customSoundRepository.insertCustomSound(id, displayName, uri, addedAt)
}

