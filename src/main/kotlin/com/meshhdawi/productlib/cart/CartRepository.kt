package com.meshhdawi.productlib.cart

import org.springframework.data.jpa.repository.JpaRepository

interface CartRepository : JpaRepository<CartEntity, Long> {

    fun findByUserId(userId: Long): List<CartEntity>

}
