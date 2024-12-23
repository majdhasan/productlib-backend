package com.meshhdawi.productlib.cart

import org.springframework.data.jpa.repository.JpaRepository
import com.meshhdawi.productlib.products.ProductEntity

interface CartItemRepository : JpaRepository<CartItemEntity, Long> {
    fun findByCartAndProduct(cart: CartEntity, product: ProductEntity): CartItemEntity?

    fun findByCartAndProductAndNotes(cart: CartEntity, product: ProductEntity, notes: String?): CartItemEntity?

}
