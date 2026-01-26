package pavball.hr.whitenoise.domain.usecase.user

import pavball.hr.whitenoise.repositories.UserRepository

internal interface LoginUseCase {
    suspend operator fun invoke(email: String, pass: String): Result<Unit>
}

internal class Login(private val userRepository: UserRepository) : LoginUseCase {
    override suspend fun invoke(email: String, pass: String): Result<Unit> =
        userRepository.login(email, pass)
}