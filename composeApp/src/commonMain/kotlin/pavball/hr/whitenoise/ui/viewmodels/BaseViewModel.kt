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

    internal val viewModelScope = CoroutineScope(
        Dispatchers.Main.immediate +
                SupervisorJob() +
                CoroutineExceptionHandler { coroutineContext, throwable ->
                    println("Exception in $this viewModelScope[$coroutineContext]: $throwable")
                }
    )

    private val _viewState = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = 6,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val viewState: SharedFlow<T> = _viewState

    protected fun runCommand(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(block = block)

    protected fun query(block: suspend CoroutineScope.() -> Flow<T>) =
        viewModelScope.launch {
            block().collect { state ->
                _viewState.emit(state)
            }
        }

    protected suspend fun emit(state: T) = _viewState.emit(state)

    inline fun <reified R : T> viewState(): Flow<R> =
        viewState.filterIsInstance<R>()

    open fun close() {
        viewModelScope.cancel("Closing ViewModel")
    }
}



