package com.meshhdawi.productlib.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Long> {
}
