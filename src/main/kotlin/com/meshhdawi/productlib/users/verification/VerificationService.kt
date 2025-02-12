package com.meshhdawi.productlib.users.verification

import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.users.UserEntity
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class VerificationService(
    private val verificationTokenRepository: VerificationTokenRepository,
    private val emailService: EmailService

) {

    fun createRandomToken(digits: Int): String {
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
            subject = "تأكيد الحساب",
            text = """
            <h1>مرحباً $firstName,</h1>
            <p>هذه رسالة تأكيد من مشهداوي.</p>
            <p><a href="https://api.meshhdawi.com/users/verify/$id?token=$token">اضغط هنا لتأكيد حسابك</a></p>
            <p>اضغط على الرابط أو انسخه والصقه في متصفحك.</p>
            
            https://api.meshhdawi.com/users/verify/$id?token=$token
            
            <p>سينتهي صلاحية هذا الرمز خلال 24 ساعة.</p>
            
            <p>إذا لم تطلب هذا الرمز، يرجى تجاهل هذه الرسالة.</p>
            <p>شكراً لك.</p>
            """.trimIndent(),
            isHtml = true
        )
    }
}