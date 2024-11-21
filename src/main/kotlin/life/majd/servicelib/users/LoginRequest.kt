package life.majd.servicelib.users

data class LoginRequest(
    val email: String,
    val password: String
)