package io.holunda.axon.camunda.example.travel.compensation.process.delegate

import io.holunda.axon.camunda.example.travel.compensation.process.MessageBasedTravelProcessWithCompensation
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
open class ExecutePayment : JavaDelegate {
  override fun execute(execution: DelegateExecution) {

    val reservation = execution.getVariable(MessageBasedTravelProcessWithCompensation.Variables.RESERVATION) as Reservation

    if (reservation.flightNumber.equals("LH-123")) {
      throw BpmnError(MessageBasedTravelProcessWithCompensation.Errors.ERROR_EXECUTING_PAYMENT)
    } else {
      execution.setVariable(MessageBasedTravelProcessWithCompensation.Variables.PAYED, true)
    }
  }

}
