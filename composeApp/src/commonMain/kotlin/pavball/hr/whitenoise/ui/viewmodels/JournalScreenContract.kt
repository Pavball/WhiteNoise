package pavball.hr.whitenoise.ui.viewmodels

import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.domain.model.JournalType

// --------------------------------------------------------------------
// ||                 NOTES (list)                                   ||
// --------------------------------------------------------------------
data class NotesViewState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoaded: Boolean = false
)

internal abstract class NotesViewModel : BaseViewModel<NotesViewState>(NotesViewState()) {
    abstract fun deleteEntry(id: Long)
}

// --------------------------------------------------------------------
// ||                 JOURNAL EDITOR (Dreams / sleep note)           ||
// --------------------------------------------------------------------
data class JournalEditorViewState(
    val id: Long? = null,          // null while writing a new entry
    val type: JournalType = JournalType.DREAM,
    val text: String = "",
    val rating: Int = 0,
    val isLoaded: Boolean = false, // true once start() has filled in the entry
    val isSaved: Boolean = false
) {
    val canSave: Boolean get() = text.isNotBlank()
}

internal abstract class JournalEditorViewModel :
    BaseViewModel<JournalEditorViewState>(JournalEditorViewState()) {
    /** Starts a new entry of [type], or loads entry [id] for editing. */
    abstract fun start(type: JournalType, id: Long?)
    abstract fun updateText(text: String)
    abstract fun updateRating(rating: Int)
    abstract fun save()
}
