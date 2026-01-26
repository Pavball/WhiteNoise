package pavball.hr.whitenoise.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.User
import pavball.hr.whitenoise.domain.model.toUser
import pavball.hr.whitenoise.source.local.LocalDataSource

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, username: String): Result<Unit>
    suspend fun logout()
}

internal class UserRepositoryImpl(
    private val localDataSource: LocalDataSource
) : UserRepository {

    override fun observeCurrentUser(): Flow<User?> {
        return localDataSource.getLoggedInUser().map { dbUser ->
            dbUser?.toUser()
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val user = localDataSource.getUserByEmail(email)
            if (user != null && user.password == password) {
                localDataSource.setLoginState(user.id, true)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return try {
            val existing = localDataSource.getUserByEmail(email)
            if (existing != null) {
                return Result.failure(Exception("User already exists"))
            }

            val newId = kotlin.random.Random.nextLong().toString()

            // Insert user
            localDataSource.insertUser(newId, email, password, username)
            // Set as logged in
            localDataSource.setLoginState(newId, true)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            // This returns the error to the ViewModel so the loading animation stops
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            localDataSource.setLoginState(null, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}