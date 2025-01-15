package com.meshhdawi.productlib.web

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class RateLimitingFilter : OncePerRequestFilter() {

    private val requestCounts = ConcurrentHashMap<String, Pair<Long, Int>>()
    private val maxRequests = 500 // Max requests per minute
    private val timeWindow = TimeUnit.MINUTES.toMillis(1) // 1-minute window

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = request.remoteAddr
        val currentTime = System.currentTimeMillis()

        val requestData = requestCounts[clientIp]
        if (requestData == null || currentTime - requestData.first > timeWindow) {
            // Reset counter if it's a new client or window has expired
            requestCounts[clientIp] = Pair(currentTime, 1)
        } else {
            val requestCount = requestData.second + 1
            if (requestCount > maxRequests) {
                response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                response.writer.write("Too many requests. Please try again later.")
                return
            }
            requestCounts[clientIp] = Pair(requestData.first, requestCount)
        }

        filterChain.doFilter(request, response)
    }
}
