package io.holunda.axon.camunda.example.travel.process

import io.holunda.axon.camunda.example.travel.process.payload.Reservation
import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

/**
 * Booking preparation service
 */
@Component
class BookingPreparationDelegate : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {

    val reservation = execution.getVariable(CommonVariables.RESERVATION) as Reservation
    execution.setVariable(CommonVariables.RESERVATION_ID, reservation.id)
    logger.info { "Prepared reservation ${reservation.id}" }
  }
}
