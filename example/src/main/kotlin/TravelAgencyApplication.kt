package io.holunda.axon.camunda.example

import io.holunda.axon.camunda.EnableAxonCamunda
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

fun main(args: Array<String>) {
  SpringApplication.run(TravelAgencyApplication::class.java, *args)
}

@SpringBootApplication
@EnableProcessApplication
@EnableAxonCamunda
class TravelAgencyApplication {

}
