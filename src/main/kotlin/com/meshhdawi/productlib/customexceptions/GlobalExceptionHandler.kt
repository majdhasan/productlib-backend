package com.meshhdawi.productlib.customexceptions

import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val message: String = ex.message ?: "No message provided"
        logger.warn("IllegalArgumentException: {}", message) // Log warning level for client errors
        val response = mapOf("error" to message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST) // HTTP 400: Bad Request
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        logger.error("Unhandled Exception: {}", ex.message, ex) // Log error level for server-side issues
        val response = mapOf("error" to "An unexpected error occurred. Please try again later.")
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: ExpiredJwtException): ResponseEntity<Map<String, String>> {
        val message: String = "Token has expired. Please refresh your token."
        logger.warn("ExpiredJwtException: {}", ex.message) // Log warning level for expired token
        val response = mapOf("error" to message)
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED) // HTTP 401: Unauthorized
    }
}
