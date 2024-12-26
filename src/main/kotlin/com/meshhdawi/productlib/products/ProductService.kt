package com.meshhdawi.productlib.products

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@org.springframework.stereotype.Service
@Transactional
class ProductService(private val repository: ProductRepository) {

    fun getAllProducts(): List<ProductEntity> = repository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        repository.findById(serviceId).orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }
}
