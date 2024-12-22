package com.meshhdawi.productlib.cart


import org.springframework.web.bind.annotation.*
import com.meshhdawi.productlib.products.ProductService
import com.meshhdawi.productlib.users.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/cart")
class CartController(
    private val cartService: CartService,
    private val cartItemService: CartItemService,
    private val productService: ProductService,
    private val userService: UserService,
    private val cartRepository: CartRepository
) {

    @GetMapping("/{userId}")
    fun getCart(@PathVariable userId: Long): ResponseEntity<CartEntity?> {
        val cart = cartService.getCartByUserId(userId)
        return if (cart != null) {
            ResponseEntity.ok(cart) // Return 200 OK with the cart data
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) // Return 404 Not Found if no cart exists
        }
    }

    @PostMapping
    fun createCart(@RequestParam userId: Long): ResponseEntity<CartEntity> {
        // Retrieve the UserEntity using the userId
        val user = userService.getUserById(userId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) // Return 404 if user not found

        // Create a new cart with the UserEntity
        val newCart = CartEntity(
            user = user,
            items = mutableListOf() // Initialize with an empty list of items
        )

        // Save the cart to the database
        val savedCart = cartRepository.save(newCart)

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart) // Return 201 Created with the new cart
    }


    // Add a product to the cart
    @PostMapping("/add")
    fun addToCart(
        @RequestParam userId: Long,
        @RequestParam productId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartItemEntity> {
        val user = userService.getUserById(userId)
        val cart = cartService.getCartByUser(user)
        val product = productService.getProductsById(productId)
        val cartItem = cartItemService.addItemToCart(cart, product, quantity)
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

    // Clear the cart
    @DeleteMapping("/clear")
    fun clearCart(@RequestParam userId: Long): ResponseEntity<Void> {
        val user = userService.getUserById(userId)
        val cart = cartService.getCartByUser(user)
        cartService.clearCart(cart)
        return ResponseEntity.noContent().build()
    }
}
