package pavball.hr.whitenoise.domain.usecase.journal

import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.repositories.JournalRepository

internal interface GetJournalEntryUseCase {
    suspend operator fun invoke(id: Long): JournalEntry?
}

internal class GetJournalEntry(private val repository: JournalRepository) : GetJournalEntryUseCase {
    override suspend fun invoke(id: Long): JournalEntry? = repository.getJournalEntry(id)
}
