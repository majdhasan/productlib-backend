package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.cart.CartService
import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.context.getUserRole
import com.meshhdawi.productlib.customexceptions.ErrorResponse
import com.meshhdawi.productlib.web.security.AuthService
import com.meshhdawi.productlib.web.security.JwtUtil
import com.meshhdawi.productlib.web.security.RefreshTokenRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val cartService: CartService,
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        val authenticatedUser = service.authenticateUser(loginRequest.email, loginRequest.password)

        val user = authenticatedUser["user"]!! as UserEntity
        val token = authenticatedUser["token"]!! as String
        val cart = cartService.getOrCreateCartByUser(user.id)

        return ResponseEntity.ok(mapOf("user" to user, "cart" to cart, "token" to token))
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserEntity> {
        return ResponseEntity.ok(service.createUser(request))
    }

    @PutMapping
    fun updateUser(@RequestBody userUpdateRequest: UserUpdateRequest, request: HttpServletRequest): ResponseEntity<Any> =
        authService.validateJWTAuth(request) {
            val userId = getUserId()
            if (getUserRole() != UserRole.ADMIN && userUpdateRequest.id != userId) {
                return@validateJWTAuth ResponseEntity.badRequest()
                    .body(ErrorResponse(message = "You can only update your own account"))
            }
            return@validateJWTAuth ResponseEntity.ok(service.updateUser(userUpdateRequest))
        }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long, request: HttpServletRequest) =
        authService.validateJWTAuth(request) {
            if (getUserId() != userId && getUserRole() != UserRole.ADMIN) {
                return@validateJWTAuth ResponseEntity.badRequest()
                    .body(ErrorResponse(message = "You can only delete your own account"))
            }
            service.deleteUser(userId)
            return@validateJWTAuth ResponseEntity.noContent().build()
        }

    @PutMapping("/{userId}/password")
    fun changeUserPassword(
        @RequestBody changePasswordRequest: ChangePasswordRequest,
        request: HttpServletRequest,
        @PathVariable userId: Long
    ): ResponseEntity<Any> = authService.validateJWTAuth(request) {
        if (getUserId() != userId && getUserRole() != UserRole.ADMIN) {
            return@validateJWTAuth ResponseEntity.badRequest()
                .body(ErrorResponse(message = "You can only change your own password"))
        }
        return@validateJWTAuth service.changePassword(changePasswordRequest, userId)
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
            return ResponseEntity.badRequest().body(
                """
                        <html>
                            <head><title>Email Verification failed</title></head>
                            <body>
                            <h1>Meshhdawi</h1>
                                <h3>Email Verification failed</h3>
                                <p>${e.message}</p>
                            </body>
                        </html>
            """.trimIndent()
            )
        }

    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<Map<String, String>> {
        val token = service.refreshToken(refreshTokenRequest.token)
        return ResponseEntity.ok(mapOf("token" to token))
    }

    // forgot password
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody forgotPasswordRequest: ForgotPasswordRequest): ResponseEntity<Any> {
        service.forgotPassword(forgotPasswordRequest.email)
        return ResponseEntity.noContent().build()
    }
}
