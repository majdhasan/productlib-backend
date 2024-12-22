package com.meshhdawi.productlib.cart

import org.springframework.data.jpa.repository.JpaRepository
import com.meshhdawi.productlib.users.UserEntity

interface CartRepository : JpaRepository<CartEntity, Long> {

    fun findByUser(user: UserEntity): CartEntity?

    fun findByUserId(userId: Long): CartEntity?

    fun findByUserIdAndStatusIs(userId: Long, status: CartStatus): CartEntity?

}
