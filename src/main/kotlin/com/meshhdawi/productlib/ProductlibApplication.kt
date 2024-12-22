package com.meshhdawi.productlib

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductLibApplication

fun main(args: Array<String>) {
	runApplication<ProductLibApplication>(*args)
}