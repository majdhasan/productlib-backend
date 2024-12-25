package com.meshhdawi.productlib.cart

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(private val repository: CartRepository) {

    fun getCartById(cartId: Long): CartEntity {
        return repository.findById(cartId).orElseThrow { IllegalArgumentException("Cart not found") }
    }

    fun findCartByUserIdAndStatus(userId: Long, status: CartStatus): CartEntity? {
        return repository.findByUserIdAndStatusIs(userId, status)
    }
}
