package pavball.hr.whitenoise.domain.model

import pavball.hr.whitenoise.db.Journal_entry

enum class JournalType { DREAM, NOTE }

/** A dream from the Dreams screen or a free-form sleep note, listed in Notes. */
data class JournalEntry(
    val id: Long,
    val type: JournalType,
    val createdAt: Long,   // epoch millis
    val rating: Int,       // 0..5 stars
    val text: String
)

internal fun Journal_entry.toJournalEntry() =
    JournalEntry(
        id = id,
        type = JournalType.entries.firstOrNull { it.name == type } ?: JournalType.NOTE,
        createdAt = created_at,
        rating = rating.toInt(),
        text = text
    )
