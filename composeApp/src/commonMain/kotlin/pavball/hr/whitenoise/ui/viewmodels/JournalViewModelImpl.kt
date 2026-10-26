package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.flow.collectLatest
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.domain.usecase.journal.DeleteJournalEntryUseCase
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntriesUseCase
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntryUseCase
import pavball.hr.whitenoise.domain.usecase.journal.SaveJournalEntryUseCase
import pavball.hr.whitenoise.ui.components.utils.nowEpochMillis

internal class NotesViewModelImpl(
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase
) : NotesViewModel() {

    init {
        runCommand {
            getJournalEntriesUseCase().collectLatest { entries ->
                updateState { copy(entries = entries, isLoaded = true) }
            }
        }
    }

    override fun deleteEntry(id: Long) {
        runCommand { deleteJournalEntryUseCase(id) }
    }
}

internal class JournalEditorViewModelImpl(
    private val getJournalEntryUseCase: GetJournalEntryUseCase,
    private val saveJournalEntryUseCase: SaveJournalEntryUseCase
) : JournalEditorViewModel() {

    private var startedFor: Pair<JournalType, Long?>? = null

    override fun start(type: JournalType, id: Long?) {
        // Called on every (re)composition of the screen; only load once.
        if (startedFor == type to id) return
        startedFor = type to id
        runCommand {
            val entry = id?.let { getJournalEntryUseCase(it) }
            updateState {
                JournalEditorViewState(
                    id = entry?.id,
                    type = entry?.type ?: type,
                    text = entry?.text.orEmpty(),
                    rating = entry?.rating ?: 0,
                    isLoaded = true
                )
            }
        }
    }

    override fun updateText(text: String) {
        runCommand { updateState { copy(text = text) } }
    }

    override fun updateRating(rating: Int) {
        runCommand { updateState { copy(rating = rating) } }
    }

    override fun save() {
        val state = getCurrentState()
        if (!state.canSave || state.isSaved) return
        runCommand {
            saveJournalEntryUseCase(state.id, state.type, state.rating, state.text, nowEpochMillis())
            updateState { copy(isSaved = true) }
        }
    }
}
