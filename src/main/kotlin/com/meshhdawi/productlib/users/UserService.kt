package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.cart.CartEntity
import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.cart.CartService
import com.meshhdawi.productlib.cart.CartStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val cartRepository: CartRepository,
    private val cartService: CartService
) {

    fun createUser(userEntity: UserEntity): UserEntity {
        validateUserUniqueness(userEntity)
        return repository.save(userEntity)
    }

    fun getOrCreateCartForUser(user: UserEntity): CartEntity {
        val existingCart = cartService.findCartByUserIdAndStatus(user.id, CartStatus.PENDING)
        return existingCart ?: cartRepository.save(CartEntity(user = user, items = mutableListOf()))    }

    fun updateUserProfile(id: Long, updatedUser: ProfileUpdateRequest): UserEntity {
        val existingUser = repository.findById(id).orElseThrow {
            IllegalArgumentException("User with ID $id not found")
        }

        // Update the fields only if provided in the update request
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

        // Save and return the updated user
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
    fun getUserById(userId: Long): UserEntity {
        return repository.findById(userId).orElseThrow { IllegalArgumentException("User with ID $userId not found") }
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
