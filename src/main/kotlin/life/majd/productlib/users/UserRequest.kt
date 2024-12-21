package life.majd.productlib.users

import java.time.LocalDate

data class UserRequest(
    val email: String,
    val password: String?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: LocalDate?
)