package com.meshhdawi.productlib.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repository: UserRepository) {

    fun createUser(userEntity: UserEntity): UserEntity {
        validateUserUniqueness(userEntity)
        return repository.save(userEntity)
    }


    fun updateUserProfile(id: Long, updatedUser: UserEntity): UserEntity {
        val existingUser = repository.findById(id).orElseThrow {
            IllegalArgumentException("User with ID $id not found")
        }

        existingUser.firstName = updatedUser.firstName
        existingUser.lastName = updatedUser.lastName
        existingUser.dateOfBirth = updatedUser.dateOfBirth

        return repository.save(existingUser)
    }

    fun authenticateUser(email: String, password: String): UserEntity? {
        val user = repository.findByEmail(email)
            ?: throw IllegalArgumentException("User with email $email not found")

        // Assuming passwords are stored securely (e.g., hashed), use a password verification function
        if (user.password != password) {
            throw IllegalArgumentException("Invalid email or password")
        }
        return user
    }

    fun verifyUser(userId: Long) {
        val user = repository.findById(userId).orElseThrow {
            IllegalArgumentException("User with ID $userId not found")
        }
        val updatedUser = user.copy(isVerified = true)
        repository.save(updatedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserEntity? {
        return repository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserEntity? {
        return repository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserEntity> {
        return repository.findAll()
    }

    fun deleteUser(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("User with ID $id not found")
        }
    }

    private fun validateUserUniqueness(userEntity: UserEntity) {
        if (repository.findByEmail(userEntity.email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }
    }
}
