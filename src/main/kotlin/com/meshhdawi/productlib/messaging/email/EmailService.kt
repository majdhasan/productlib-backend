package com.meshhdawi.productlib.messaging.email

import com.meshhdawi.productlib.AppProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender, private val appProperties: AppProperties) {

    fun sendEmail(to: String, subject: String, text: String, isHtml: Boolean = false) {
        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(text, isHtml)
            helper.setFrom(appProperties.emailConfig.email, "Meshhdawi")

            mailSender.send(message)
            println("Email sent successfully to $to")
        } catch (e: Exception) {
            println("Error sending email: ${e.message}")
            e.printStackTrace()
            throw IllegalStateException("Failed to send email")
        }
    }
}
