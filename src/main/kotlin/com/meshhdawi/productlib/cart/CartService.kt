package com.meshhdawi.productlib.cart

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.meshhdawi.productlib.users.UserEntity
import java.time.LocalDateTime

@Service
@Transactional
class CartService(private val repository: CartRepository) {

    fun createCart(user: UserEntity): CartEntity {
        val cart = CartEntity(user = user, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        return repository.save(cart)
    }

    fun saveCart(cart: CartEntity): CartEntity {
        return repository.save(cart)
    }


    fun getCartByUserId(userId: Long): CartEntity? {
        // Fetch the cart for the given userId using the repository
        return repository.findByUserId(userId)
    }

    fun getCartByUser(user: UserEntity): CartEntity {
        return repository.findByUser(user)
            ?: throw IllegalArgumentException("Cart for user ${user.id} not found")
    }

    fun clearCart(cart: CartEntity) {
        cart.items.clear()
        repository.save(cart)
    }
}
