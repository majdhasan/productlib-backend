package com.meshhdawi.productlib.web.security

import com.meshhdawi.productlib.AppProperties
import com.meshhdawi.productlib.users.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil(appProperties: AppProperties) {

    private final var secret: String = appProperties.jwtSecret
    var secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun generateToken(userId: Long, role: UserRole): String {
        val claims: Map<String, Any> = mapOf("userId" to userId, "role" to role)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)) // 10 days
            .signWith(secretKey)
            .compact()
    }

    fun extractUserId(token: String): Long = extractAllClaims(token).get("userId", Integer::class.java)?.toLong()
        ?: throw IllegalArgumentException("Invalid token")

    fun extractUserRole(token: String): UserRole = UserRole.valueOf(
        extractAllClaims(token).get("role", String::class.java) ?: throw IllegalArgumentException("Invalid token")
    )

    fun validateToken(token: String): Boolean {
        return try {
            extractAllClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            throw e
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

    fun refreshToken(token: String): String? {
        return try {
            val claims = extractAllClaims(token)
            val userId = (claims["userId"] as Int).toLong()
            generateToken(userId, UserRole.valueOf(claims["role"] as String))
        } catch (e: ExpiredJwtException) {
            val claims = e.claims
            val userId = (claims["userId"] as Int).toLong()
            generateToken(userId, UserRole.valueOf(claims["role"] as String))
        } catch (e: Exception) {
            null
        }
    }
}