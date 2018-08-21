package io.holunda.axon.camunda.example.delegate

import io.holunda.axon.camunda.example.Reservation
import io.holunda.axon.camunda.example.TravelProcess
import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Booking preparation service
 */
@Component
class BookingPreparationService : JavaDelegate {

  companion object: KLogging()

  override fun execute(execution: DelegateExecution) {

    val reservation = Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "astoria", "LH-124")

    execution.setVariable(TravelProcess.Variables.RESERVATION, reservation)
    execution.setVariable(TravelProcess.Variables.RESERVATION_ID, reservation.id)

    logger.info { "Prepared reservation ${reservation.id}" }

  }
}
