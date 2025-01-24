package com.meshhdawi.productlib

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class ProductLibApplication

fun main(args: Array<String>) {
	runApplication<ProductLibApplication>(*args)
}