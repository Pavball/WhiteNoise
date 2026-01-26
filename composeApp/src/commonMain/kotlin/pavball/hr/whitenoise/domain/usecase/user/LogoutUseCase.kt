package pavball.hr.whitenoise.domain.usecase.user

import pavball.hr.whitenoise.repositories.UserRepository

internal interface LogoutUseCase {
    suspend operator fun invoke()
}

internal class Logout(private val userRepository: UserRepository) : LogoutUseCase {
    override suspend fun invoke() = userRepository.logout()
}