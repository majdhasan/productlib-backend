package com.meshhdawi.productlib.users

import org.mindrot.jbcrypt.BCrypt

data class CreateUserRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String,
    val agreeToReceiveMessages: Boolean
){
    fun toUserEntity(): UserEntity {
        return UserEntity(
            email = email.trimIndent().lowercase(),
            firstName = firstName,
            lastName = lastName,
            password = BCrypt.hashpw(password, BCrypt.gensalt()),
            phoneNumber = phoneNumber,
            agreeToReceiveMessages = agreeToReceiveMessages
        )
    }
}