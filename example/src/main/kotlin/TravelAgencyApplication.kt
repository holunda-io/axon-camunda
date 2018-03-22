package io.holunda.axon.camunda.example

import io.holunda.axon.camunda.CamundaAxonConfiguration
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

fun main(args: Array<String>) {
  SpringApplication.run(TravelAgencyApplication::class.java, *args)
}

@SpringBootApplication
@EnableProcessApplication
@Import(value = arrayOf(CamundaAxonConfiguration::class))
class TravelAgencyApplication {

}
