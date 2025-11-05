package pavball.hr.whitenoise.ui.viewmodels

import androidx.lifecycle.ViewModel
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

internal abstract class BaseViewModel<T> : ViewModel() {
    private val _viewState = MutableSharedFlow<T>(replay = 1)
    val viewState: SharedFlow<T> = _viewState

    protected suspend fun emit(state: T) = _viewState.emit(state)

    fun <T> viewState() = viewState as Flow<T>

    open fun close() {}
}


