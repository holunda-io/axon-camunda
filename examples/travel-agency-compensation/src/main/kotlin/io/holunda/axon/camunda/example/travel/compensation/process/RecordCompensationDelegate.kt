package io.holunda.axon.camunda.example.travel.compensation.process

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
open class RecordCompensation : JavaDelegate {
  override fun execute(execution: DelegateExecution) {
    execution.removeVariable(MessageBasedTravelProcessWithCompensation.Variables.HOTEL_CONFIRMATION_CODE)
    execution.removeVariable(MessageBasedTravelProcessWithCompensation.Variables.TICKET_NUMBER)
  }

}
