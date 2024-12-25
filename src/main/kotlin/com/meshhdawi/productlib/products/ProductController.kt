package com.meshhdawi.productlib.products

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val service: ProductService) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductEntity>> {
        val products = service.getAllProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductEntity> = ResponseEntity.ok(service.getProductsById(id))
}
