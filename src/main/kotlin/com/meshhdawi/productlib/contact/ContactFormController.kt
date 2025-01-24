package com.meshhdawi.productlib.contact

import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/contact")
class ContactFormController(
    private val contactFormService: ContactFormService
) {

    private val submissionCounts = ConcurrentHashMap<String, Int>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate({
            submissionCounts.clear()
        }, 24, 24, TimeUnit.HOURS)
    }

    @PostMapping
    fun storeContactForm(
        @RequestBody contactForm: ContactForm,
        @RequestHeader("X-Forwarded-For", required = false) clientIp: String?
    ): ResponseEntity<Resource> {
        val ip = clientIp ?: "unknown"

        val count = submissionCounts.getOrDefault(ip, 0)
        if (count >= 10) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
        }

        submissionCounts[ip] = count + 1
        contactFormService.saveContactForm(contactForm)
        return ResponseEntity.ok().build()
    }
}