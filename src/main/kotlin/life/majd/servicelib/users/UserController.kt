package life.majd.servicelib.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val service: UserService) {

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserEntity> {
        val user = service.authenticateUser(loginRequest.email, loginRequest.password)
            ?: return ResponseEntity.status(401).body(null) // Unauthorized
        return ResponseEntity.ok(user)
    }

    @GetMapping("/profile/{id}")
    fun getUserProfile(@PathVariable id: Long): ResponseEntity<UserEntity> {
        val user = service.getUserById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    @PutMapping("/profile/{id}")
    fun updateUserProfile(@PathVariable id: Long, @RequestBody updatedUser: UserEntity): ResponseEntity<UserEntity> {
        val user = service.updateUserProfile(id, updatedUser)
        return ResponseEntity.ok(user)
    }

    @PostMapping
    fun createUser(@RequestBody userEntity: UserEntity): ResponseEntity<UserEntity> {
        return ResponseEntity.ok(service.createUser(userEntity))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserEntity> {
        val user = service.getUserById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    @GetMapping
    fun getAllUsers(
        @RequestParam(required = false) email: String?
    ): ResponseEntity<Any> {
        return when {

            email != null -> {
                val user = service.getUserByEmail(email) ?: return ResponseEntity.notFound().build()
                ResponseEntity.ok(user)
            }
            else -> ResponseEntity.ok(service.getAllUsers())
        }
    }

    @PostMapping("/{id}/verify")
    fun verifyUser(@PathVariable id: Long): ResponseEntity<Void> {
        service.verifyUser(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
