package life.majd.productlib.customexceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(SlotUnavailableException::class)
    fun handleSlotUnavailableException(ex: SlotUnavailableException): ResponseEntity<Map<String, String>> {
        val message: String = ex.message ?: ""
        val response = mapOf("error" to message)
        return ResponseEntity(response, HttpStatus.CONFLICT) // HTTP 409: Conflict
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val message: String = ex.message ?: ""
        val response = mapOf("error" to message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST) // HTTP 400: Bad Request
    }

    // Handle other exceptions (optional)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val response = mapOf("error" to "An unexpected error occurred. Please try again later.")
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
    }
}