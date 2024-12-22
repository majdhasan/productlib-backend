package com.meshhdawi.productlib.cart

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.meshhdawi.productlib.products.ProductEntity
import java.time.LocalDateTime

@Service
@Transactional
class CartItemService(private val repository: CartItemRepository) {

    fun getCartItemById(id: Long): CartItemEntity {
        return repository.findById(id).orElseThrow { IllegalArgumentException("Cart item with ID $id not found") }
    }

    fun addItemToCart(cart: CartEntity, product: ProductEntity, quantity: Int): CartItemEntity {
        val existingItem = repository.findByCartAndProduct(cart, product)
        if (existingItem != null) {
            // Update quantity if the product already exists in the cart
            existingItem.quantity += quantity
            return repository.save(existingItem)
        } else {
            // Create a new cart item
            val cartItem = CartItemEntity(
                cart = cart,
                product = product,
                quantity = quantity,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            return repository.save(cartItem)
        }
    }

    fun updateItemQuantity(cartItem: CartItemEntity, newQuantity: Int): CartItemEntity {
        cartItem.quantity = newQuantity
        cartItem.updatedAt = LocalDateTime.now()
        return repository.save(cartItem)
    }

    fun removeItemFromCart(cartItem: CartItemEntity) {
        repository.delete(cartItem)
    }
}
