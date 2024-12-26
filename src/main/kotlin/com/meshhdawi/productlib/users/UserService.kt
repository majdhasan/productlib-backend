package com.meshhdawi.productlib.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val repository: UserRepository
) {

    fun createUser(userEntity: UserEntity): UserEntity {
        validateUserUniqueness(userEntity)
        return repository.save(userEntity)
    }

    fun updateUserProfile(id: Long, updatedUser: ProfileUpdateRequest): UserEntity {
        val existingUser = getUserById(id)

        updatedUser.firstName?.let {
            existingUser.firstName = it
        }
        updatedUser.lastName?.let {
            existingUser.lastName = it
        }
        updatedUser.dateOfBirth?.let {
            existingUser.dateOfBirth = it
        }
        updatedUser.preferredLanguage?.let {
            existingUser.preferredLanguage = it
        }

        return repository.save(existingUser)
    }

    fun authenticateUser(email: String, password: String): UserEntity {
        val user: UserEntity = repository.findByEmail(email)
            ?: throw IllegalArgumentException("User with email $email not found")

        // Assuming passwords are stored securely (e.g., hashed), use a password verification function
        if (user.password != password) {
            throw IllegalArgumentException("Invalid email or password")
        }
        return user
    }

    @Transactional(readOnly = true)
    fun getUserById(userId: Long): UserEntity {
        return repository.findById(userId).orElseThrow { IllegalArgumentException("User with ID $userId not found") }
    }

    private fun validateUserUniqueness(userEntity: UserEntity) {
        if (repository.findByEmail(userEntity.email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }
    }
}
