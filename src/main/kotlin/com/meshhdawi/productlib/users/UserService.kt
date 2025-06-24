package com.meshhdawi.productlib.users

import com.meshhdawi.productlib.customexceptions.ErrorResponse
import com.meshhdawi.productlib.customexceptions.TokenExpiredException
import com.meshhdawi.productlib.customexceptions.TokenNotFoundException
import com.meshhdawi.productlib.customexceptions.UserNotFoundException
import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.users.verification.VerificationService
import com.meshhdawi.productlib.users.verification.VerificationTokenRepository
import com.meshhdawi.productlib.web.security.JwtUtil
import org.mindrot.jbcrypt.BCrypt
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val verificationService: VerificationService,
    private val jwtUtil: JwtUtil,
    private val emailService: EmailService
) {


    private val resetAttempts = ConcurrentHashMap<String, Long>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate({
            resetAttempts.clear()
        }, 24, 24, TimeUnit.HOURS)
    }

    private fun normalizeEmail(email: String) = email.trim().lowercase()

    private fun validatePassword(password: String) {
        if (password.length < 6) {
            throw IllegalArgumentException("Password must be at least 6 characters long")
        }
    }

    fun createUser(request: CreateUserRequest): UserEntity {
        val email = normalizeEmail(request.email)
        validateUserUniqueness(email)
        validatePassword(request.password)

        val sanitizedRequest = request.copy(email = email)
        val savedUser = repository.save(sanitizedRequest.toUserEntity())
        verificationService.createVerificationToken(savedUser)
        return savedUser
    }

    fun updateUser(userUpdateRequest: UserUpdateRequest): UserEntity {
        val user = getUserById(userUpdateRequest.id)
        return repository.save(
            user.copy(
                firstName = userUpdateRequest.firstName,
                lastName = userUpdateRequest.lastName,
                phoneNumber = userUpdateRequest.phoneNumber,
                agreeToReceiveMessages = userUpdateRequest.agreeToReceiveMessages
            )
        )
    }

    fun authenticateUser(email: String, password: String): Map<String, Any> {
        val normalizedEmail = normalizeEmail(email)
        val user: UserEntity = repository.findByEmail(normalizedEmail)
            ?: throw IllegalArgumentException("User with email $email not found")

        if (!BCrypt.checkpw(password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return mapOf("token" to jwtUtil.generateToken(user.id, user.role), "user" to user)
    }

    @Transactional(readOnly = true)
    fun getUserById(userId: Long): UserEntity {
        return repository.findById(userId).orElseThrow { IllegalArgumentException("User with ID $userId not found") }
    }

    private fun validateUserUniqueness(email: String) {
        val normalizedEmail = normalizeEmail(email)
        if (repository.findByEmail(normalizedEmail) != null) {
            throw IllegalArgumentException("الايميل مستخدم من قبل، هل نسيت كلمة المرور؟")
        }
    }

    fun verifyUser(userId: Long, token: String) {
        val user = repository.findById(userId).orElseThrow {
            throw UserNotFoundException("User not found with id: $userId")
        }

        val verificationTokens = verificationTokenRepository.findByUserId(userId)

        val retrievedToken = verificationTokens.find { it.token == token }
            ?: throw TokenNotFoundException("Token not found or already has been used")

        if (retrievedToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw TokenExpiredException("Verification token has expired for user id: $userId")
        }

        user.emailVerified = true
        verificationTokenRepository.delete(retrievedToken)
        repository.save(user)
    }

    fun deleteUser(userId: Long) = repository.deleteById(userId)

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun changePassword(userId: Long, newPassword: String) {
        val user = getUserById(userId)
        user.password = BCrypt.hashpw(newPassword, BCrypt.gensalt())
        repository.save(user)
    }

    fun changePassword(changePasswordRequest: ChangePasswordRequest, userId: Long): ResponseEntity<Any> {
        val user = getUserById(userId)
        if (user.password == null || !checkPassword(changePasswordRequest.oldPassword, user.password!!)) {
            return ResponseEntity.badRequest().body(ErrorResponse(message = "Invalid old password"))
        }
        validatePassword(changePasswordRequest.newPassword)
        changePassword(userId, changePasswordRequest.newPassword)
        return ResponseEntity.noContent().build()
    }

    fun refreshToken(jwtToken: String): String {
        return jwtUtil.refreshToken(jwtToken) ?: throw IllegalArgumentException("Invalid token")
    }

    fun forgotPassword(email: String) {
        val normalizedEmail = normalizeEmail(email)
        val user = repository.findByEmail(normalizedEmail)
            ?: throw IllegalArgumentException("User with email $email not found")

        if (resetAttempts.containsKey(normalizedEmail)) {
            throw IllegalArgumentException("Password reset already requested. Please try again after 24 hours.")
        }
        verificationService.createRandomToken(6).also { token ->
            user.password = BCrypt.hashpw(token, BCrypt.gensalt())
            repository.save(user).run {
                sendNewPasswordEmail(user, token)
            }
            resetAttempts[normalizedEmail] = System.currentTimeMillis()
        }
    }

    private fun sendNewPasswordEmail(user: UserEntity, newPassword: String) {
        emailService.sendEmail(
            to = user.email,
            subject = "تغيير كلمة المرور",
            text = """
                <h1> مرحباً ${user.firstName},</h1>
                <p>كلمة المرور الجديدة هي: $newPassword</p>
                <p>يرجى تغييرها بعد تسجيل الدخول.</p>
                
                <p>شكراً لاستخدامكم خدماتنا.</p>
                <p>فريق المشهداوي</p>
            """.trimIndent(),

            isHtml = true
        )
    }

    fun getUsersByRole(role: UserRole): MutableList<UserEntity> {
        return repository.findByRole(role)
    }
}
