package pavball.hr.whitenoise.repositorites
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.model.toCustomSound
import pavball.hr.whitenoise.source.local.LocalDataSource
internal interface CustomSoundRepository {
    fun getCustomSounds(): Flow<List<CustomSound>>
    suspend fun insertCustomSound(id: String, displayName: String, uri: String, addedAt: Long)
    suspend fun deleteCustomSound(id: String)
    suspend fun updateCustomSound(id: String, displayName: String)
}

internal class CustomSoundRepositoryImpl(
    private val localDataSource: LocalDataSource
) : CustomSoundRepository {

    override fun getCustomSounds(): Flow<List<CustomSound>> {
        return localDataSource.getAllCustomSounds().map { list -> list.map {
            it.toCustomSound() }
        }
    }

    override suspend fun insertCustomSound(id: String, displayName: String, uri: String, addedAt: Long) =
        localDataSource.insertCustomSoundById(id = id, displayName = displayName, uri = uri, addedAt = addedAt)

    override suspend fun deleteCustomSound(id: String) =
        localDataSource.deleteCustomSoundById(soundId = id)

    override suspend fun updateCustomSound(id: String, displayName: String) {
        localDataSource.updateCustomSoundNameById(id, displayName)
    }
}
