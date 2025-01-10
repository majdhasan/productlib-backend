package com.meshhdawi.productlib.cart

import com.meshhdawi.productlib.cart.cartitems.CartItemRepository
import com.meshhdawi.productlib.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val repository: CartRepository,
    private val userService: UserService,
    private val cartItemRepository: CartItemRepository
) {

    fun emptyCart(cart: CartEntity) =
        cart.items.forEach {
            cartItemRepository.delete(it)
        }

    fun getCartById(cartId: Long): CartEntity {
        return repository.findById(cartId).orElseThrow { IllegalArgumentException("Cart not found") }
    }

    fun getOrCreateCartByUser(userId: Long): CartEntity {
        val user = userService.getUserById(userId)
        val cartList = repository.findByUserId(userId)

        if (cartList.isNotEmpty()) return cartList.last()
        return repository.save(CartEntity(user = user, items = mutableListOf()))
    }
}
