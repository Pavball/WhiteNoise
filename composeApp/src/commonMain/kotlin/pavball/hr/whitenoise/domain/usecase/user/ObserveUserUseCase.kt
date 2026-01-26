package pavball.hr.whitenoise.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.User
import pavball.hr.whitenoise.repositories.UserRepository

internal interface ObserveUserUseCase {
    operator fun invoke(): Flow<User?>
}

internal class ObserveUser(private val userRepository: UserRepository) : ObserveUserUseCase {
    override fun invoke(): Flow<User?> = userRepository.observeCurrentUser()
}