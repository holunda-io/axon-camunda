package io.holunda.axon.camunda.example.travel.compensation.process

import io.holunda.axon.camunda.example.travel.process.CommonVariables
import io.holunda.axon.camunda.example.travel.process.payload.Reservation
import mu.KLogging
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class ExecutePaymentDelegate : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {

    val reservation = execution.getVariable(CommonVariables.RESERVATION) as Reservation

    if (reservation.flightNumber == "LH-123") {
      logger.warn { "Payment failed... Time to compensate." }
      throw BpmnError(MessageBasedTravelProcessWithCompensation.Errors.ERROR_EXECUTING_PAYMENT)
    } else {
      logger.info { "Payment was successful ${reservation.flightNumber}, ${reservation.hotel}." }
      execution.setVariable(MessageBasedTravelProcessWithCompensation.Variables.PAYED, true)
    }
  }

}
