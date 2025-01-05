package com.meshhdawi.productlib.web

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FaviconController {

    @GetMapping("/favicon.ico")
    fun ignoreFavicon(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_NO_CONTENT // 204 No Content
    }
}
