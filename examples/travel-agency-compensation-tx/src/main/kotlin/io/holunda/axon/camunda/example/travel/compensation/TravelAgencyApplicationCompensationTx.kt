package io.holunda.axon.camunda.example.travel.compensation

import io.holunda.axon.camunda.EnableAxonCamunda
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.EnableTransactionManagement

fun main(args: Array<String>) {
  SpringApplication.run(TravelAgencyApplicationCompensationTx::class.java, *args)
}

@SpringBootApplication
@EnableProcessApplication
@EnableAxonCamunda
@EnableTransactionManagement
class TravelAgencyApplicationCompensationTx {
  @Bean
  fun eventStorageEngine() = InMemoryEventStorageEngine()
}

