package pavball.hr.whitenoise.domain.usecase.journal

import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.repositories.JournalRepository

internal interface SaveJournalEntryUseCase {
    /** Inserts a new entry when [id] is null, otherwise updates that entry. */
    suspend operator fun invoke(id: Long?, type: JournalType, rating: Int, text: String, now: Long)
}

internal class SaveJournalEntry(private val repository: JournalRepository) : SaveJournalEntryUseCase {
    override suspend fun invoke(id: Long?, type: JournalType, rating: Int, text: String, now: Long) {
        val cleanRating = rating.coerceIn(0, 5)
        if (id == null) repository.insertJournalEntry(type, now, cleanRating, text.trim())
        else repository.updateJournalEntry(id, cleanRating, text.trim())
    }
}
