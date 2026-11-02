package pavball.hr.whitenoise.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.SoundMix
import pavball.hr.whitenoise.domain.model.toSoundMix
import pavball.hr.whitenoise.source.local.LocalDataSource

internal interface SoundMixRepository {
    fun getSoundMixes(): Flow<List<SoundMix>>
    suspend fun insertSoundMix(
        name: String,
        leadSoundId: String,
        leadVolume: Float,
        layers: Map<String, Float>,
        createdAt: Long
    )
    suspend fun deleteSoundMix(id: Long)
}

internal class SoundMixRepositoryImpl(
    private val localDataSource: LocalDataSource
) : SoundMixRepository {

    override fun getSoundMixes(): Flow<List<SoundMix>> =
        localDataSource.getAllSoundMixes().map { list -> list.map { it.toSoundMix() } }

    override suspend fun insertSoundMix(
        name: String,
        leadSoundId: String,
        leadVolume: Float,
        layers: Map<String, Float>,
        createdAt: Long
    ) = localDataSource.insertSoundMix(
        name = name,
        leadSoundId = leadSoundId,
        leadVolume = leadVolume.toDouble(),
        layers = SoundMix.encodeLayers(layers),
        createdAt = createdAt
    )

    override suspend fun deleteSoundMix(id: Long) =
        localDataSource.deleteSoundMixById(id)
}
