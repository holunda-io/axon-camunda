package io.holunda.axon.camunda.example.process

import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

/**
 * Booking preparation service
 */
@Component
class BookingPreparationService : JavaDelegate {

  companion object: KLogging()

  override fun execute(execution: DelegateExecution) {

    val reservation = execution.getVariable(MessageBasedTravelProcess.Variables.RESERVATION) as Reservation
    execution.setVariable(MessageBasedTravelProcess.Variables.RESERVATION_ID, reservation.id)
    logger.info { "Prepared reservation ${reservation.id}" }
  }
}
