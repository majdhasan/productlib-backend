package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.cart.CartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val service: UserService,
    private val cartService: CartService
) {

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        val user = service.authenticateUser(loginRequest.email, loginRequest.password)
        val cart = cartService.getOrCreateCartByUser(user.id)
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
