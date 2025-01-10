package com.meshhdawi.productlib.products

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(private val repository: ProductRepository) {

    fun createProduct(product: ProductEntity): ProductEntity = repository.save(product)

    fun updateProduct(id: Long, product: ProductEntity): ProductEntity {
        val existingProduct = getProductsById(id)
        return repository.save(
            existingProduct.copy(
                name = product.name,
                translations = product.translations,
                image = product.image,
                description = product.description,
                cost = product.cost,
                category = product.category,
                updatedAt = product.updatedAt
            )
        )
    }

    fun getAllProducts(): List<ProductEntity> = repository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        repository.findById(serviceId).orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }

    fun deleteProduct(id: Long) = repository.deleteById(id)
}
