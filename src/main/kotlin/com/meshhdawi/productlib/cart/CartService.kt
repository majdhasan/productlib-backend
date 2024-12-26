package com.meshhdawi.productlib.cart

import com.meshhdawi.productlib.users.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val repository: CartRepository,
    private val userService: UserService) {

    fun getCartById(cartId: Long): CartEntity {
        return repository.findById(cartId).orElseThrow { IllegalArgumentException("Cart not found") }
    }

    fun getOrCreateCartByUser(userId: Long): CartEntity {
        val user = userService.getUserById(userId)
        val cartList = repository.findByUserIdAndStatusIs(userId, CartStatus.PENDING)

        if (cartList.isNotEmpty()) return cartList.last()
        return repository.save(CartEntity(user = user, items = mutableListOf()))
    }
}
