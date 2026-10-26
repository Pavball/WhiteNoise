package pavball.hr.whitenoise.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.SleepSession
import pavball.hr.whitenoise.domain.model.toSleepSession
import pavball.hr.whitenoise.source.local.LocalDataSource

internal interface SleepRepository {
    fun getCompletedSessions(): Flow<List<SleepSession>>
    fun getActiveSession(): Flow<SleepSession?>
    suspend fun startSession(startedAt: Long, alarmAt: Long?)
    suspend fun endSession(id: Long, endedAt: Long)
    suspend fun rateSession(id: Long, quality: Int)
    suspend fun deleteSession(id: Long)
}

internal class SleepRepositoryImpl(
    private val localDataSource: LocalDataSource
) : SleepRepository {

    override fun getCompletedSessions(): Flow<List<SleepSession>> =
        localDataSource.getCompletedSleepSessions().map { list -> list.map { it.toSleepSession() } }

    override fun getActiveSession(): Flow<SleepSession?> =
        localDataSource.getActiveSleepSession().map { it?.toSleepSession() }

    override suspend fun startSession(startedAt: Long, alarmAt: Long?) =
        localDataSource.insertSleepSession(startedAt, alarmAt)

    override suspend fun endSession(id: Long, endedAt: Long) =
        localDataSource.endSleepSession(id, endedAt)

    override suspend fun rateSession(id: Long, quality: Int) =
        localDataSource.rateSleepSession(id, quality.toLong())

    override suspend fun deleteSession(id: Long) =
        localDataSource.deleteSleepSessionById(id)
}
