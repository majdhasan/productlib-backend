package com.meshhdawi.productlib

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["com.meshhdawi.productlib"]) // Specify the base package
@EnableJpaRepositories(basePackages = ["com.meshhdawi.productlib"]) // Specify repository packages
class ProductlibApplication

fun main(args: Array<String>) {
	runApplication<ProductlibApplication>(*args)
}
