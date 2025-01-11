package com.meshhdawi.productlib.cart

import com.meshhdawi.productlib.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val repository: CartRepository,
    private val userService: UserService,
) {
    fun deleteCart(cartId: Long) {
        val cart = getCartById(cartId)
        repository.delete(cart)
    }

    fun getCartById(cartId: Long): CartEntity {
        return repository.findById(cartId).orElseThrow { IllegalArgumentException("Cart not found") }
    }

    fun getOrCreateCartByUser(userId: Long): CartEntity {
        val user = userService.getUserById(userId)
        val foundCart = repository.findByUserId(userId)

        return foundCart ?: repository.save(CartEntity(user = user, items = mutableListOf()))
    }
}
