package life.majd.productlib.products

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@org.springframework.stereotype.Service
@Transactional
class ProductService(private val repository: ProductRepository) {

    fun createProduct(productEntity: ProductEntity): ProductEntity {
        validateProduct(productEntity)
        return repository.save(productEntity)
    }

    fun getAllProducts(): List<ProductEntity> = repository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        repository.findById(serviceId).orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }

    fun updateProduct(id: Long, updatedProductEntity: ProductEntity): ProductEntity {
        val existingProduct = repository.findById(id).orElseThrow {
            IllegalArgumentException("Product with ID $id not found")
        }
        val serviceToSave = existingProduct.copy(
            name = updatedProductEntity.name,
            description = updatedProductEntity.description,
            cost = updatedProductEntity.cost,
            updatedAt = LocalDateTime.now()
        )
        return repository.save(serviceToSave)
    }

    fun deleteProduct(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Product with ID $id not found")
        }
    }

    private fun validateProduct(productEntity: ProductEntity) {
        if (productEntity.cost <= 0) {
            throw IllegalArgumentException("Cost must be greater than zero.")
        }
    }
}
