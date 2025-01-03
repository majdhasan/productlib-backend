package com.meshhdawi.productlib.users

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
    private val passwordEncoder: BCryptPasswordEncoder

) {

    fun createUser(request: CreateUserRequest): UserEntity {
        validateUserUniqueness(request.email)
        val hashedPassword = passwordEncoder.encode(request.password)
        val userEntity = UserEntity(
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            password = hashedPassword,
            phoneNumber = request.phoneNumber
        )
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

        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }
        return user
    }

    @Transactional(readOnly = true)
    fun getUserById(userId: Long): UserEntity {
        return repository.findById(userId).orElseThrow { IllegalArgumentException("User with ID $userId not found") }
    }

    private fun validateUserUniqueness(email: String) {
        if (repository.findByEmail(email) != null) {
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
