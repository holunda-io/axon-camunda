package io.holunda.axon.camunda.example.travel.compensation.process

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

    val reservation = execution.getVariable(MessageBasedTravelProcessWithCompensation.Variables.RESERVATION) as Reservation
    execution.setVariable(MessageBasedTravelProcessWithCompensation.Variables.RESERVATION_ID, reservation.id)
    logger.info { "Prepared reservation ${reservation.id}" }
  }
}
