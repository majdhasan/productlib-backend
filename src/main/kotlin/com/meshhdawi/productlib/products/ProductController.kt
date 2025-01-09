package com.meshhdawi.productlib.products

import com.meshhdawi.productlib.context.getUserRole
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val service: ProductService,
    private val authService: AuthService
) {

    @PostMapping
    fun createProduct(product: ProductEntity, request: HttpServletRequest): ResponseEntity<ProductEntity> =
        authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to create a product.")
            ResponseEntity.ok(service.createProduct(product))
        }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductEntity>> = ResponseEntity.ok(service.getAllProducts())

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductEntity> =
        ResponseEntity.ok(service.getProductsById(id))
}
