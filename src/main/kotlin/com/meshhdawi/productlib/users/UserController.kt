package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.cart.CartService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/verify/{userId}")
    fun verifyUserEmail(@PathVariable userId: Long, @RequestParam token: String): ResponseEntity<String> {
        try {
            service.verifyUser(userId, token)
            val htmlResponse = """
        <html>
            <head><title>Email Verified</title></head>
            <body>
                <h1>Email Verified</h1>
                <p>You can now log in to your account.</p>
            </body>
        </html>
    """.trimIndent()
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("""
                        <html>
                            <head><title>Email Verification failed</title></head>
                            <body>
                            <h1>Meshhdawi</h1>
                                <h3>Email Verification failed</h3>
                                <p>${e.message}</p>
                            </body>
                        </html>
            """.trimIndent())
        }

    }
}
