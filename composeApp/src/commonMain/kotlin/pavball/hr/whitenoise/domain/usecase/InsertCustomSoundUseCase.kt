package pavball.hr.whitenoise.domain.usecase


import pavball.hr.whitenoise.repositories.CustomSoundRepository

internal interface InsertCustomSoundUseCase {

    suspend operator fun invoke(
        id: String,
        displayName: String,
        uri: String,
        addedAt: Long,
        colorId: String
    )
}

internal class InsertCustomSound(private val customSoundRepository: CustomSoundRepository) :
    InsertCustomSoundUseCase {
    override suspend fun invoke(
        id: String,
        displayName: String,
        uri: String,
        addedAt: Long,
        colorId: String
    ) =
        customSoundRepository.insertCustomSound(id, displayName, uri, addedAt, colorId)
}

