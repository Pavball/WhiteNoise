package pavball.hr.whitenoise.domain.usecase.journal

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.repositories.JournalRepository

internal interface GetJournalEntriesUseCase {
    operator fun invoke(): Flow<List<JournalEntry>>
}

internal class GetJournalEntries(private val repository: JournalRepository) : GetJournalEntriesUseCase {
    override fun invoke(): Flow<List<JournalEntry>> = repository.getJournalEntries()
}
