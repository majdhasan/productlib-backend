package life.majd.servicelib.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repository: UserRepository) {

    fun createUser(user: User): User {
        if (repository.findByUsername(user.username) != null) {
            throw IllegalArgumentException("Username already exists.")
        }
        if (repository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }
        return repository.save(user)
    }

    fun getUserById(id: Long): User? {
        return repository.findById(id).orElse(null)
    }

    fun getUserByUsername(username: String): User? {
        return repository.findByUsername(username)
    }

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
}
