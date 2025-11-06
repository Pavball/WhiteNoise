package pavball.hr.whitenoise.ui.viewmodels

import androidx.lifecycle.ViewModel
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

internal abstract class BaseViewModel<T>(
    initialState: T
) : ViewModel(), Closeable {

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

    private var currentState: T = initialState

    protected fun runCommand(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(block = block)

    protected fun query(block: suspend CoroutineScope.() -> Flow<T>) =
        viewModelScope.launch {
            block().collect { state ->
                currentState = state
                _viewState.emit(state)
            }
        }

    protected suspend fun emit(state: T) {
        currentState = state
        _viewState.emit(state)
    }

    /**
     * Generic helper to modify and emit new state using a reducer lambda.
     */
    protected suspend fun updateState(reducer: T.() -> T) {
        val newState = currentState.reducer()
        currentState = newState
        _viewState.emit(newState)
    }

    protected fun getCurrentState(): T = currentState

    inline fun <reified R : T> viewState(): Flow<R> =
        viewState.filterIsInstance<R>()

    override fun close() {
        viewModelScope.cancel("Closing ViewModel")
    }
}
