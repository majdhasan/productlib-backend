package com.meshhdawi.productlib.products

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(private val service: ProductService) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductEntity>> = ResponseEntity.ok(service.getAllProducts())

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductEntity> =
        ResponseEntity.ok(service.getProductsById(id))
}
