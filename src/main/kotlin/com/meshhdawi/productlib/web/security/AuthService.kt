package com.meshhdawi.productlib.web.security

import com.meshhdawi.productlib.context.UserContext
import com.meshhdawi.productlib.context.UserContextHolder
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil
) {
    fun <T> validateJWTAuth(
        request: HttpServletRequest,
        lambda: () -> ResponseEntity<T>,
    ): ResponseEntity<T> {
        val authorizationHeader = request.getHeader("Authorization")
            ?: throw IllegalArgumentException("Authorization header is missing")
        val token = authorizationHeader.removePrefix("Bearer ")
        if (!jwtUtil.validateToken(token)) {
            throw IllegalArgumentException("Invalid JWT token")
        }
        val userId = jwtUtil.extractUserId(token)
        val role = jwtUtil.extractUserRole(token)
        UserContextHolder.setUserContext(UserContext(userId, role))
        return try {
            lambda()
        } finally {
            UserContextHolder.clear()
        }
    }
}