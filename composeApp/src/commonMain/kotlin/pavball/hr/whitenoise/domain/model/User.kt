package pavball.hr.whitenoise.domain.model

import pavball.hr.whitenoise.db.User

data class User(
    val id: String,
    val email: String,
    val username: String?
)

internal fun User.toUser() =
    pavball.hr.whitenoise.domain.model.User(
        id = id,
        email = email,
        username = username
    )
