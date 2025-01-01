package com.meshhdawi.productlib.messaging.email

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController(private val emailService: EmailService) {

    @GetMapping("/send-email")
    fun sendTestEmail(@RequestParam to: String, @RequestParam subject: String, @RequestParam text: String): String {
        emailService.sendEmail(to, subject, text, isHtml = true)
        return "Email sent to $to"
    }
}
