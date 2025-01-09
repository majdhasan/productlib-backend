package com.meshhdawi.productlib.products

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(private val repository: ProductRepository) {

    fun createProduct(product: ProductEntity): ProductEntity = repository.save(product)

    fun getAllProducts(): List<ProductEntity> = repository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        repository.findById(serviceId).orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }
}
