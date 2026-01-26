package pavball.hr.whitenoise.domain.usecase.user

import pavball.hr.whitenoise.repositories.UserRepository

internal interface RegisterUseCase {
    suspend operator fun invoke(email: String, pass: String, username: String): Result<Unit>
}

internal class Register(private val userRepository: UserRepository) : RegisterUseCase {
    override suspend fun invoke(email: String, pass: String, username: String): Result<Unit> =
        userRepository.register(email, pass, username)
}