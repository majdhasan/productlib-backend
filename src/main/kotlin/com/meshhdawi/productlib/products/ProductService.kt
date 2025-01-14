package com.meshhdawi.productlib.products

import com.meshhdawi.productlib.fileupload.FileStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productTranslationRepository: ProductTranslationRepository,
    private val storageService: FileStorageService
) {

    fun createProduct(productRequest: ProductCreateRequest): ProductEntity {
        if (productRequest.image.isEmpty) {
            throw IllegalArgumentException("Image is required")
        }
        val storeFileName = storageService.storeImageVariations(productRequest.image)

        val product = ProductEntity(
            name = productRequest.name,
            description = productRequest.description,
            price = productRequest.price,
            image = storeFileName,
            unit = productRequest.unit
        )

        val savedProduct = productRepository.save(product)

        val translations = productRequest.translations.map {
            ProductTranslationEntity(
                product = savedProduct,
                language = it.language,
                name = it.name,
                description = it.description
            )
        }

        productTranslationRepository.saveAll(translations)

        return productRepository.findById(savedProduct.id)
            .orElseThrow { IllegalArgumentException("There was an issue saving the product") }
    }

    fun updateProduct(id: Long, product: ProductEntity): ProductEntity {
        val existingProduct = getProductsById(id)
        return productRepository.save(
            existingProduct.copy(
                name = product.name,
                translations = product.translations,
                image = product.image,
                description = product.description,
                price = product.price,
                updatedAt = product.updatedAt
            )
        )
    }

    fun getAllProducts(): List<ProductEntity> = productRepository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        productRepository.findById(serviceId)
            .orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }

    fun deleteProduct(id: Long) = productRepository.deleteById(id)
}
