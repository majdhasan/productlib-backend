package com.meshhdawi.productlib.cart.cartitems

import com.meshhdawi.productlib.cart.CartEntity
import com.meshhdawi.productlib.products.ProductEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CartItemService(private val repository: CartItemRepository) {

    fun getCartItemById(id: Long): CartItemEntity {
        return repository.findById(id).orElseThrow { IllegalArgumentException("Cart item with ID $id not found") }
    }

    fun addItemToCart(cart: CartEntity, product: ProductEntity, notes: String?, quantity: Int): CartItemEntity {
        // Check for an existing item with the same product and notes in the cart
        val existingItem = repository.findByCartAndProductAndNotes(cart, product, notes)

        if (existingItem != null) {
            // Update quantity if an item with the same product and notes exists
            existingItem.quantity += quantity
            existingItem.updatedAt = LocalDateTime.now()
            return repository.save(existingItem)
        } else {
            // Create a new cart item
            val cartItem = CartItemEntity(
                cart = cart,
                product = product,
                quantity = quantity,
                notes = notes,
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
