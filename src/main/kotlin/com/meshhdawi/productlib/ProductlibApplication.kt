package com.meshhdawi.productlib

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class ProductLibApplication

fun main(args: Array<String>) {
	runApplication<ProductLibApplication>(*args)
}