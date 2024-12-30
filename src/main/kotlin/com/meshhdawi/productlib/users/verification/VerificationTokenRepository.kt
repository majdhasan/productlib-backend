package com.meshhdawi.productlib.users.verification

import org.springframework.data.jpa.repository.JpaRepository

interface VerificationTokenRepository : JpaRepository<VerificationTokenEntity, Long> {

    fun findByUserId(userId: Long): List<VerificationTokenEntity>
}