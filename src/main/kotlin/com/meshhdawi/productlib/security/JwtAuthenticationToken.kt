package com.meshhdawi.productlib.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthenticationToken(
    private val userId: Long
) : AbstractAuthenticationToken(null) {

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = userId
}