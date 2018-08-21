package io.holunda.axon.camunda.example.delegate

import io.holunda.axon.camunda.example.airline.Airport
import io.holunda.axon.camunda.example.airline.FlightCreated
import io.holunda.axon.camunda.example.airline.FlightNumber
import io.holunda.axon.camunda.example.hotel.HotelCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Component
class LoggingService : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {
    logger.info { "Executed in ${execution.activityInstanceId}" }
    execution.variables.forEach { name, value -> logger.info { "\t$name : '$value'" } }
  }
}


