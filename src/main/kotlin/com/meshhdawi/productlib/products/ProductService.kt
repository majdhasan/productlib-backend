package com.meshhdawi.productlib.products

import com.meshhdawi.productlib.fileupload.FileStorageService
import com.meshhdawi.productlib.products.translations.ProductTranslationEntity
import com.meshhdawi.productlib.products.translations.ProductTranslationRepository
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
            unit = productRequest.unit,
            category = productRequest.category
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

    fun updateProduct(id: Long, productUpdateRequest: ProductUpdateRequest): ProductEntity {
        val existingProduct = getProductsById(id)
        return productRepository.save(
            existingProduct.copy(
                name = productUpdateRequest.name,
                description = productUpdateRequest.description,
                price = productUpdateRequest.price,
                unit = productUpdateRequest.unit,
                category = productUpdateRequest.category,
            )
        )
    }

//    fun updateProductImage(id: Long, image: MultipartFile): ProductEntity {
//        val existingProduct = getProductsById(id)
//        val storeFileName = storageService.storeImageVariations(image)
//        return productRepository.save(
//            existingProduct.copy(
//                image = storeFileName
//            )
//        )
//    }

    fun getAllProducts(): List<ProductEntity> = productRepository.findAll()

    fun getProductsById(serviceId: Long): ProductEntity =
        productRepository.findById(serviceId)
            .orElseThrow { IllegalArgumentException("Product with ID $serviceId not found") }

    fun deleteProduct(id: Long) = productRepository.deleteById(id)
}
