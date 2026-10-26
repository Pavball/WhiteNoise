package pavball.hr.whitenoise.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.domain.model.toJournalEntry
import pavball.hr.whitenoise.source.local.LocalDataSource

internal interface JournalRepository {
    fun getJournalEntries(): Flow<List<JournalEntry>>
    suspend fun getJournalEntry(id: Long): JournalEntry?
    suspend fun insertJournalEntry(type: JournalType, createdAt: Long, rating: Int, text: String)
    suspend fun updateJournalEntry(id: Long, rating: Int, text: String)
    suspend fun deleteJournalEntry(id: Long)
}

internal class JournalRepositoryImpl(
    private val localDataSource: LocalDataSource
) : JournalRepository {

    override fun getJournalEntries(): Flow<List<JournalEntry>> =
        localDataSource.getAllJournalEntries().map { list -> list.map { it.toJournalEntry() } }

    override suspend fun getJournalEntry(id: Long): JournalEntry? =
        localDataSource.getJournalEntryById(id)?.toJournalEntry()

    override suspend fun insertJournalEntry(type: JournalType, createdAt: Long, rating: Int, text: String) =
        localDataSource.insertJournalEntry(type.name, createdAt, rating.toLong(), text)

    override suspend fun updateJournalEntry(id: Long, rating: Int, text: String) =
        localDataSource.updateJournalEntry(id, rating.toLong(), text)

    override suspend fun deleteJournalEntry(id: Long) =
        localDataSource.deleteJournalEntryById(id)
}
