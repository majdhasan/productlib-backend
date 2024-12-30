package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.customexceptions.TokenExpiredException
import com.meshhdawi.productlib.customexceptions.TokenNotFoundException
import com.meshhdawi.productlib.customexceptions.UserNotFoundException
import com.meshhdawi.productlib.users.verification.VerificationService
import com.meshhdawi.productlib.users.verification.VerificationTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val verificationService: VerificationService,
) {

    fun createUser(userEntity: UserEntity): UserEntity {
        validateUserUniqueness(userEntity)
        val savedUser = repository.save(userEntity)
        verificationService.createVerificationToken(savedUser)
        return savedUser
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

    fun verifyUser(userId: Long, token: String) {
        val user = repository.findById(userId).orElseThrow {
            throw UserNotFoundException("User not found with id: $userId")
        }

        val verificationTokens = verificationTokenRepository.findByUserId(userId)

        val retrievedToken = verificationTokens.find { it.token == token }
            ?: throw TokenNotFoundException("Token not found or already has been used")

        if (retrievedToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw TokenExpiredException("Verification token has expired for user id: $userId")
        }

        user.emailVerified = true
        verificationTokenRepository.delete(retrievedToken)
        repository.save(user)
    }
}
