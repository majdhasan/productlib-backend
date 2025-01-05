package com.meshhdawi.productlib.cart


import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.products.ProductService
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
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
    private val authService: AuthService
) {
    @GetMapping("/{cartId}")
    fun getCartById(@PathVariable cartId: Long, request: HttpServletRequest): ResponseEntity<CartEntity?> =
        authService.validateJWTAuth(request) {
            val cart = cartService.getCartById(cartId)
            if (getUserId() != cart.user.id) throw IllegalArgumentException("You are not authorized to view this cart.")
            ResponseEntity.ok(cart)
        }

    @PostMapping("/user/{userId}")
    fun getOrCreateCart(@PathVariable userId: Long, request: HttpServletRequest): ResponseEntity<CartEntity> =
        authService.validateJWTAuth(request) {
            if (getUserId() != userId) throw IllegalArgumentException("You are not authorized to view this cart.")
            ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.getOrCreateCartByUser(userId))
        }


    @PostMapping("/add")
    fun addToCart(
        @RequestBody cartItemRequest: CartItemRequest,
        request: HttpServletRequest
    ): ResponseEntity<CartItemEntity> =
        authService.validateJWTAuth(request) {
            if (getUserId() != cartItemRequest.userId) throw IllegalArgumentException("You are not authorized to add items to this cart.")
            val cart = cartService.getOrCreateCartByUser(cartItemRequest.userId)
            val product = productService.getProductsById(cartItemRequest.productId)
            val cartItem = cartItemService.addItemToCart(cart, product, cartItemRequest.notes, cartItemRequest.quantity)
            ResponseEntity.ok(cartItem)
        }

    // Update the quantity of an item in the cart
    @PutMapping("/update/{cartItemId}")
    fun updateCartItem(
        @PathVariable cartItemId: Long,
        @RequestParam newQuantity: Int,
        request: HttpServletRequest
    ): ResponseEntity<CartItemEntity> =
        authService.validateJWTAuth(request) {
            val cartItemById = cartItemService.getCartItemById(cartItemId)
            if (cartItemById.cart.user.id != getUserId()) throw IllegalArgumentException("You are not authorized to update this cart item.")
            val cartItem = cartItemService.updateItemQuantity(cartItemById, newQuantity)
            ResponseEntity.ok(cartItem)
        }


    // Remove an item from the cart
    @DeleteMapping("/remove/{cartItemId}")
    fun removeFromCart(@PathVariable cartItemId: Long, request: HttpServletRequest): ResponseEntity<Void> =
        authService.validateJWTAuth(request) {
            val cartItem = cartItemService.getCartItemById(cartItemId)
            if (cartItem.cart.user.id != getUserId()) throw IllegalArgumentException("You are not authorized to remove this item from the cart.")
            cartItemService.removeItemFromCart(cartItem)
            ResponseEntity.noContent().build()
        }

}
