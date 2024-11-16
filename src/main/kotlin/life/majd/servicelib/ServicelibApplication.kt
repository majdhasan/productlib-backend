package life.majd.servicelib

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["life.majd.servicelib"]) // Specify the base package
@EnableJpaRepositories(basePackages = ["life.majd.servicelib"]) // Specify repository packages
class ServicelibApplication

fun main(args: Array<String>) {
	runApplication<ServicelibApplication>(*args)
}
