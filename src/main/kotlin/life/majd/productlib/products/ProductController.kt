package life.majd.productlib.products

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val service: ProductService) {

    @PostMapping
    fun createProduct(@RequestBody productEntity: ProductEntity): ResponseEntity<ProductEntity> {
        val createdProduct = this.service.createProduct(productEntity)
        return ResponseEntity.ok(createdProduct)
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductEntity>> {
        val products = service.getAllProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductEntity> = ResponseEntity.ok(service.getProductsById(id))

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody updatedProductEntity: ProductEntity): ResponseEntity<ProductEntity> {
        val updated = service.updateProduct(id, updatedProductEntity)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
