package com.meshhdawi.productlib.cart.cartitems

import com.meshhdawi.productlib.cart.CartEntity
import org.springframework.data.jpa.repository.JpaRepository
import com.meshhdawi.productlib.products.ProductEntity

interface CartItemRepository : JpaRepository<CartItemEntity, Long> {

    fun findByCartAndProductAndNotes(cart: CartEntity, product: ProductEntity, notes: String?): CartItemEntity?

}
