package life.majd.servicelib.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repository: UserRepository) {

    fun createUser(user: User): User {
        validateUserUniqueness(user)
        return repository.save(user)
    }

    fun createGuestUser(email: String): User {
        if (repository.findByEmail(email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }

        val guestUser = User(
            email = email,
            password = null,
            isRegistered = false,
            isVerified = false
        )
        return repository.save(guestUser)
    }

    fun authenticateUser(email: String, password: String): User? {
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
    fun getUserById(id: Long): User? {
        return repository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): User? {
        return repository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> {
        return repository.findAll()
    }

    fun deleteUser(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("User with ID $id not found")
        }
    }

    private fun validateUserUniqueness(user: User) {
        if (repository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }
    }
}
