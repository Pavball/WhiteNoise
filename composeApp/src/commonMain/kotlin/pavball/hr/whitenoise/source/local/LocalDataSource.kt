package pavball.hr.whitenoise.source.local
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.db.Custom_sounds
import pavball.hr.whitenoise.db.User

internal interface LocalDataSource {

    fun getAllCustomSounds(): Flow<List<Custom_sounds>>

    suspend fun deleteCustomSoundById(soundId: String)

    suspend fun insertCustomSoundById(id: String, displayName: String, uri:
    String, addedAt: Long, colorId: String)

    suspend fun updateCustomSoundNameById(id: String, displayName: String)

    suspend fun updateCustomSoundColorById(id: String, colorId: String)

    fun getLoggedInUser(): Flow<User?>
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(id: String, email: String, pass: String, username: String)
    suspend fun setLoginState(userId: String?, isLoggedIn: Boolean)
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

    override fun getLoggedInUser(): Flow<User?> {
        return database.userQueries.getLoggedInUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            database.userQueries.getUserByEmail(email).executeAsOneOrNull()
        }
    }

    override suspend fun insertUser(id: String, email: String, pass: String, username: String) {
        withContext(Dispatchers.IO) {
            // New users are logged in by default
            database.userQueries.insertUser(id, email, pass, username, 1)
        }
    }

    override suspend fun setLoginState(userId: String?, isLoggedIn: Boolean) {
        withContext(Dispatchers.IO) {
            if (isLoggedIn && userId != null) {
                // Logout everyone else first to ensure only one active session
                database.userQueries.logoutAll()
                database.userQueries.loginUser(userId)
            } else {
                database.userQueries.logoutAll()
            }
        }
    }

}
