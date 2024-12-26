package com.meshhdawi.productlib.cart


import com.meshhdawi.productlib.products.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cart")
class CartController(
    private val cartService: CartService,
    private val cartItemService: CartItemService,
    private val productService: ProductService,
) {

    @GetMapping("/{cartId}")
    fun getCartById(@PathVariable cartId: Long): ResponseEntity<CartEntity?> =
        ResponseEntity.ok(cartService.getCartById(cartId))

    @PostMapping("/user/{userId}")
    fun getOrCreateCart(@PathVariable userId: Long): ResponseEntity<CartEntity> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(cartService.getOrCreateCartByUser(userId))

    @PostMapping("/add")
    fun addToCart(@RequestBody cartItemRequest: CartItemRequest): ResponseEntity<CartItemEntity> {

        val cart = cartService.getOrCreateCartByUser(cartItemRequest.userId)
        val product = productService.getProductsById(cartItemRequest.productId)
        val cartItem = cartItemService.addItemToCart(cart, product, cartItemRequest.notes, cartItemRequest.quantity)
        return ResponseEntity.ok(cartItem)
    }

    // Update the quantity of an item in the cart
    @PutMapping("/update/{cartItemId}")
    fun updateCartItem(
        @PathVariable cartItemId: Long,
        @RequestParam newQuantity: Int
    ): ResponseEntity<CartItemEntity> {
        val cartItem = cartItemService.updateItemQuantity(cartItemService.getCartItemById(cartItemId), newQuantity)
        return ResponseEntity.ok(cartItem)
    }

    // Remove an item from the cart
    @DeleteMapping("/remove/{cartItemId}")
    fun removeFromCart(@PathVariable cartItemId: Long): ResponseEntity<Void> {
        val cartItem = cartItemService.getCartItemById(cartItemId)
        cartItemService.removeItemFromCart(cartItem)
        return ResponseEntity.noContent().build()
    }

}
