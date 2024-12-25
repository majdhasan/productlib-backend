package com.meshhdawi.productlib.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val service: UserService) {

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        val user = service.authenticateUser(loginRequest.email, loginRequest.password)
            ?: return ResponseEntity.status(401).body(null) // Unauthorized

        val cart = service.getOrCreateCartForUser(user)

        return ResponseEntity.ok(mapOf("user" to user, "cart" to cart))
    }

    @PutMapping("/profile/{userId}")
    fun updateProfile(
        @PathVariable userId: Long,
        @RequestBody profileUpdateRequest: ProfileUpdateRequest
    ): ResponseEntity<UserEntity> {
        val user = service.updateUserProfile(userId, profileUpdateRequest)
        return ResponseEntity.ok(user)
    }

    @PostMapping
    fun createUser(@RequestBody userEntity: UserEntity): ResponseEntity<UserEntity> {
        return ResponseEntity.ok(service.createUser(userEntity))
    }
}
