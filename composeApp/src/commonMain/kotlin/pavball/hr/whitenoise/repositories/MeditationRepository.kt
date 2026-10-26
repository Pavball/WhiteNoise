package pavball.hr.whitenoise.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.MeditationSession
import pavball.hr.whitenoise.domain.model.Mood
import pavball.hr.whitenoise.domain.model.toMeditationSession
import pavball.hr.whitenoise.source.local.LocalDataSource

internal interface MeditationRepository {
    fun getMeditationSessions(): Flow<List<MeditationSession>>
    suspend fun insertMeditationSession(mood: Mood, completedAt: Long, durationMs: Long)
}

internal class MeditationRepositoryImpl(
    private val localDataSource: LocalDataSource
) : MeditationRepository {

    override fun getMeditationSessions(): Flow<List<MeditationSession>> =
        localDataSource.getAllMeditationSessions().map { list -> list.map { it.toMeditationSession() } }

    override suspend fun insertMeditationSession(mood: Mood, completedAt: Long, durationMs: Long) =
        localDataSource.insertMeditationSession(mood.name, completedAt, durationMs)
}
