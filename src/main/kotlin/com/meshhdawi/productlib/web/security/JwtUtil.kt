package com.meshhdawi.productlib.web.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {
    // TODO make this secret key more secure
    private final var secret: String = "your-super-secure-256-bit-secret-key-12345"
    var secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun generateToken(userId: Long): String {
        val claims: Map<String, Any> = mapOf("userId" to userId)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)) // 10 days
            .signWith(secretKey)
            .compact()
    }

    fun extractUserId(token: String): Long {
        return extractAllClaims(token).get("userId", Integer::class.java)?.toLong() ?: throw IllegalArgumentException("Invalid token")
    }

    fun validateToken(token: String): Boolean {
        return try {
            extractAllClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}