package pavball.hr.whitenoise.source.local
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.db.Custom_sounds

internal interface LocalDataSource {

    fun getAllCustomSounds(): Flow<List<Custom_sounds>>

    suspend fun deleteCustomSoundById(soundId: String)

    suspend fun insertCustomSoundById(id: String, displayName: String, uri:
    String, addedAt: Long, colorId: String)

    suspend fun updateCustomSoundNameById(id: String, displayName: String)

    suspend fun updateCustomSoundColorById(id: String, colorId: String)
}

internal class LocalDataSourceImpl(private val database: Database) : LocalDataSource {

    override fun getAllCustomSounds(): Flow<List<Custom_sounds>> =
        database.queries.selectAll().asFlow().mapToList(Dispatchers.IO)

    override suspend fun insertCustomSoundById(id: String, displayName: String, uri: String, addedAt: Long, colorId: String) {
        withContext(Dispatchers.IO) { database.queries.insertSound(id,
            displayName, uri, addedAt, colorId) }
    }

    override suspend fun updateCustomSoundNameById(id: String, displayName: String) {
        withContext(Dispatchers.IO) { database.queries.updateName(displayName, id) }
    }

    override suspend fun updateCustomSoundColorById(id: String, colorId: String) {
        withContext(Dispatchers.IO) { database.queries.updateColor(colorId, id) }
    }

    override suspend fun deleteCustomSoundById(soundId: String) {
        withContext(Dispatchers.IO) { database.queries.deleteSound(soundId) }
    }

}
