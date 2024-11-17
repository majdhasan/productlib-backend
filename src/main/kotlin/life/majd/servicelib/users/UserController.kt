package life.majd.servicelib.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val service: UserService) {

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(service.createUser(user))
    }

    @PostMapping("/guest")
    fun createGuestUser(@RequestParam email: String): ResponseEntity<User> {
        return ResponseEntity.ok(service.createGuestUser(email))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
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

    @PostMapping("/{id}/convert")
    fun convertGuestToRegisteredUser(
        @PathVariable id: Long,
        @RequestBody request: ConvertGuestRequest
    ): ResponseEntity<User> {
        val updatedUser = service.convertGuestToRegisteredUser(
            guestId = id,
            password = request.password
        )
        return ResponseEntity.ok(updatedUser)
    }
}
