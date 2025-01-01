package com.meshhdawi.productlib.users.verification

import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.users.UserEntity
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import java.awt.SystemColor.text
import kotlin.random.Random

@Service
class VerificationService(
    private val verificationTokenRepository: VerificationTokenRepository,
    private val emailService: EmailService

) {

    private fun createRandomToken(digits: Int): String {
        val sb = StringBuilder(digits)
        for (i in 0 until digits) {
            sb.append(Random.nextInt(10))
        }
        return sb.toString()
    }

    fun createVerificationToken(user: UserEntity) {
        val token = createRandomToken(6)
        verificationTokenRepository.save(VerificationTokenEntity(token = token, user = user))
            .also {
                sendVerificationToken(it)
            }
    }

    private fun sendVerificationToken(verificationToken: VerificationTokenEntity) {
        val token = verificationToken.token
        val email = verificationToken.user.email
        val id = verificationToken.user.id
        val firstName = verificationToken.user.firstName

        emailService.sendEmail(
            to = email,
            subject = "Verify your account",
            text = """
                <h1>Hello $firstName,</h1>
                <p>This is a verification email from Meshhdawi.</p>
                
                <p>Please use the following token to verify your account:</p>
                
                <p><strong>$token</strong></p>
                
                <p><a href="http://localhost:8080/api/users/verify?/$id?token=$token">Click here to verify your account</a></p>
                <p> press on the link or copy and paste it in your browser.</p>
                
                http://localhost:8080/api/users/verify/$id?token=$token
                
                <p>This token will expire in 24 hours.</p> 
                
                <p>If you did not request this token, please ignore this email.</p>
                <p>Thank you.</p>
            """.trimIndent(),
            isHtml = true
        )
    }
}