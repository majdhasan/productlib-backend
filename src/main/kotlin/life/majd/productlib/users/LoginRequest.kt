package life.majd.productlib.users

data class LoginRequest(
    val email: String,
    val password: String
)