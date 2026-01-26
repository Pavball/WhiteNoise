package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.flow.collectLatest
import pavball.hr.whitenoise.domain.usecase.user.LoginUseCase
import pavball.hr.whitenoise.domain.usecase.user.LogoutUseCase
import pavball.hr.whitenoise.domain.usecase.user.ObserveUserUseCase
import pavball.hr.whitenoise.domain.usecase.user.RegisterUseCase

internal class ProfileViewModelImpl(
    private val observeUserUseCase: ObserveUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ProfileViewModel() {

    init {
        // Observe the current user from the database and update state
        runCommand {
            observeUserUseCase().collectLatest { user ->
                updateState { copy(currentUser = user) }
            }
        }
    }

    override fun login(email: String, pass: String) {
        runCommand {
            updateState { copy(authState = AuthState.Loading) }

            val result = loginUseCase(email, pass)

            updateState {
                copy(
                    authState = if (result.isSuccess) {
                        AuthState.Success
                    } else {
                        AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                    }
                )
            }
        }
    }

    override fun register(username: String, email: String, pass: String) {
        runCommand {
            updateState { copy(authState = AuthState.Loading) }

            val result = registerUseCase(email, pass, username)

            updateState {
                copy(
                    authState = if (result.isSuccess) {
                        AuthState.Success
                    } else {
                        AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
                    }
                )
            }
        }
    }

    override fun logout() {
        runCommand {
            logoutUseCase()
            resetAuthState()
        }
    }

    override fun resetAuthState() {
        runCommand {
            updateState { copy(authState = AuthState.Idle) }
        }
    }
}