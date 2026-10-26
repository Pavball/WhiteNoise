package pavball.hr.whitenoise.source.local
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.db.Custom_sounds
import pavball.hr.whitenoise.db.Journal_entry
import pavball.hr.whitenoise.db.Meditation_session
import pavball.hr.whitenoise.db.SelectCompleted
import pavball.hr.whitenoise.db.Sleep_session

internal interface LocalDataSource {

    fun getAllCustomSounds(): Flow<List<Custom_sounds>>

    suspend fun deleteCustomSoundById(soundId: String)

    suspend fun insertCustomSoundById(id: String, displayName: String, uri:
    String, addedAt: Long, colorId: String)

    suspend fun updateCustomSoundNameById(id: String, displayName: String)

    suspend fun updateCustomSoundColorById(id: String, colorId: String)

    // journal
    fun getAllJournalEntries(): Flow<List<Journal_entry>>
    suspend fun getJournalEntryById(id: Long): Journal_entry?
    suspend fun insertJournalEntry(type: String, createdAt: Long, rating: Long, text: String)
    suspend fun updateJournalEntry(id: Long, rating: Long, text: String)
    suspend fun deleteJournalEntryById(id: Long)

    // sleep
    fun getCompletedSleepSessions(): Flow<List<SelectCompleted>>
    fun getActiveSleepSession(): Flow<Sleep_session?>
    suspend fun insertSleepSession(startedAt: Long, alarmAt: Long?)
    suspend fun endSleepSession(id: Long, endedAt: Long)
    suspend fun rateSleepSession(id: Long, quality: Long)
    suspend fun deleteSleepSessionById(id: Long)

    // meditation
    fun getAllMeditationSessions(): Flow<List<Meditation_session>>
    suspend fun insertMeditationSession(mood: String, completedAt: Long, durationMs: Long)
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

    // --- journal ---

    override fun getAllJournalEntries(): Flow<List<Journal_entry>> =
        database.journalQueries.selectAll().asFlow().mapToList(Dispatchers.IO)

    override suspend fun getJournalEntryById(id: Long): Journal_entry? =
        withContext(Dispatchers.IO) { database.journalQueries.selectById(id).executeAsOneOrNull() }

    override suspend fun insertJournalEntry(type: String, createdAt: Long, rating: Long, text: String) {
        withContext(Dispatchers.IO) { database.journalQueries.insertEntry(type, createdAt, rating, text) }
    }

    override suspend fun updateJournalEntry(id: Long, rating: Long, text: String) {
        withContext(Dispatchers.IO) { database.journalQueries.updateEntry(rating, text, id) }
    }

    override suspend fun deleteJournalEntryById(id: Long) {
        withContext(Dispatchers.IO) { database.journalQueries.deleteEntry(id) }
    }

    // --- sleep ---

    override fun getCompletedSleepSessions(): Flow<List<SelectCompleted>> =
        database.sleepQueries.selectCompleted().asFlow().mapToList(Dispatchers.IO)

    override fun getActiveSleepSession(): Flow<Sleep_session?> =
        database.sleepQueries.selectActive().asFlow().mapToOneOrNull(Dispatchers.IO)

    override suspend fun insertSleepSession(startedAt: Long, alarmAt: Long?) {
        withContext(Dispatchers.IO) { database.sleepQueries.insertSession(startedAt, alarmAt) }
    }

    override suspend fun endSleepSession(id: Long, endedAt: Long) {
        withContext(Dispatchers.IO) { database.sleepQueries.endSession(endedAt, id) }
    }

    override suspend fun rateSleepSession(id: Long, quality: Long) {
        withContext(Dispatchers.IO) { database.sleepQueries.rateSession(quality, id) }
    }

    override suspend fun deleteSleepSessionById(id: Long) {
        withContext(Dispatchers.IO) { database.sleepQueries.deleteSession(id) }
    }

    // --- meditation ---

    override fun getAllMeditationSessions(): Flow<List<Meditation_session>> =
        database.meditationQueries.selectAll().asFlow().mapToList(Dispatchers.IO)

    override suspend fun insertMeditationSession(mood: String, completedAt: Long, durationMs: Long) {
        withContext(Dispatchers.IO) {
            database.meditationQueries.insertSession(mood, completedAt, durationMs)
        }
    }

}
