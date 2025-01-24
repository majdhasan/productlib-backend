package com.meshhdawi.productlib.contact

import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.users.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ContactFormService(
    private val emailService: EmailService,
    private val contactFormRepository: ContactFormRepository,
    private val userService: UserService
) {

    fun saveContactForm(contactForm: ContactForm) = contactFormRepository.save(contactForm.toEntity())

    @Scheduled(fixedRate = 3600000)
    private fun sendContactFormsFromPreviousHourToEmail() {
        fetchContactFormsFromPreviousHour().sendContactFormsToAdmins()
    }

    fun List<ContactFormEntity>.sendContactFormsToAdmins() {
        if (isEmpty()) return
        var message = """
            <h1>New Meshhdawi Contact Forms</h1>"
            <br>
            """.trimIndent()
        forEach {
            message += """
                <p>Time: ${it.createdAt}</p>
                <p>Name: ${it.name}</p>
                <p>Email: ${it.email}</p>
                <p>Message: ${it.message}</p>
                <br>
            """.trimIndent()
        }
        userService.getUsersByRole(UserRole.ADMIN).forEach {
            emailService.sendEmail(
                to = it.email,
                subject = "New Meshhdawi Contact Form",
                text = message,
                isHtml = true
            )
        }

    }

    private fun fetchContactFormsFromPreviousHour(): List<ContactFormEntity> {
        return contactFormRepository.getContactFormEntitiesByCreatedAtBetween(
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now()
        )
    }

}

