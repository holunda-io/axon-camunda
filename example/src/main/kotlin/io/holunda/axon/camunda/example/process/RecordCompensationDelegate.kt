package io.holunda.axon.camunda.example.process

import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
open class RecordCompensation : JavaDelegate {
  override fun execute(execution: DelegateExecution) {
    execution.removeVariable(MessageBasedTravelProcess.Variables.HOTEL_CONFIRMATION_CODE)
    execution.removeVariable(MessageBasedTravelProcess.Variables.TICKET_NUMBER)
  }

}
