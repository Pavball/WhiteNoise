package pavball.hr.whitenoise.domain.usecase.journal

import pavball.hr.whitenoise.repositories.JournalRepository

internal interface DeleteJournalEntryUseCase {
    suspend operator fun invoke(id: Long)
}

internal class DeleteJournalEntry(private val repository: JournalRepository) : DeleteJournalEntryUseCase {
    override suspend fun invoke(id: Long) = repository.deleteJournalEntry(id)
}
