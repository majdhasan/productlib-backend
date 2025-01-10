package com.meshhdawi.productlib.products

import com.fasterxml.jackson.databind.ObjectMapper
import com.meshhdawi.productlib.context.getUserRole
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val service: ProductService,
    private val authService: AuthService
) {

    @PostMapping
    fun createProduct(
        @RequestParam("name") name: String,
        @RequestParam("description") description: String?,
        @RequestParam("cost") cost: Double,
        @RequestParam("image") image: MultipartFile,
        @RequestParam("category") category: ProductCategory,
        @RequestParam("translations") translationsJson: String,
        request: HttpServletRequest
    ): ResponseEntity<ProductEntity> = authService.validateJWTAuth(request) {
        if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to create a product.")

        val translations =
            ObjectMapper().readValue(translationsJson, Array<ProductTranslationRequest>::class.java).toList()
        val productRequest = ProductCreateRequest(name, description, cost, image, category, translations)

        ResponseEntity.ok(service.createProduct(productRequest))
    }

    @PostMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        product: ProductEntity,
        request: HttpServletRequest
    ): ResponseEntity<ProductEntity> =
        authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to update a product.")
            ResponseEntity.ok(service.updateProduct(id, product))
        }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductEntity>> = ResponseEntity.ok(service.getAllProducts())

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductEntity> =
        ResponseEntity.ok(service.getProductsById(id))

    // delete product
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Unit> =
        authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to delete a product.")
            service.deleteProduct(id)
            ResponseEntity.noContent().build()
        }
}
