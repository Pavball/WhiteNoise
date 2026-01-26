package pavball.hr.whitenoise.ui.viewmodels

import pavball.hr.whitenoise.domain.model.User

data class ProfileViewState(
    val currentUser: User? = null,
    val authState: AuthState = AuthState.Idle
)

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

internal abstract class ProfileViewModel : BaseViewModel<ProfileViewState>(ProfileViewState()) {

    abstract fun login(email: String, pass: String)
    abstract fun register(username: String, email: String, pass: String)
    abstract fun logout()
    abstract fun resetAuthState()
}