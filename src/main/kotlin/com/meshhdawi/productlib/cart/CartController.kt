package com.meshhdawi.productlib.cart


import com.meshhdawi.productlib.products.ProductService
import com.meshhdawi.productlib.users.UserService
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
    private val userService: UserService,
    private val cartRepository: CartRepository
) {

    @GetMapping("/{cartId}")
    fun getCartById(@PathVariable cartId: Long): ResponseEntity<CartEntity?> =
        ResponseEntity.ok(cartService.getCartById(cartId))


    @PostMapping
    fun createCart(@RequestParam userId: Long): ResponseEntity<CartEntity> {
        // Retrieve the UserEntity using the userId
        val user = userService.getUserById(userId)

        // Create a new cart with the UserEntity
        val newCart = CartEntity(
            user = user,
            items = mutableListOf() // Initialize with an empty list of items
        )

        // Save the cart to the database
        val savedCart = cartRepository.save(newCart)

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart) // Return 201 Created with the new cart
    }


    @PostMapping("/add")
    fun addToCart(@RequestBody cartItemRequest: CartItemRequest): ResponseEntity<CartItemEntity> {
        // Validate the product ID
        if (cartItemRequest.productId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null) // Return 400 Bad Request if the productId is invalid
        }

        val cart = cartService.getCartById(cartItemRequest.cartId)
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
