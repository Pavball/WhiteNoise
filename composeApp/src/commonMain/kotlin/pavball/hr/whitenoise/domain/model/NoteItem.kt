package pavball.hr.whitenoise.domain.model

data class NoteItem(
    val type: String, // "Dream" or "Note"
    val date: String,
    val detail: String, // e.g., "5★" or "7h 5m"
    val fullText: String // Content for the detail view
)