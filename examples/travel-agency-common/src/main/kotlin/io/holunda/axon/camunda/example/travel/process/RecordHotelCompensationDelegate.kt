package io.holunda.axon.camunda.example.travel.process

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class RecordHotelCompensationDelegate : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {
    logger.info { "Compensating hotel booking" }
    execution.removeVariable(CommonVariables.HOTEL_CONFIRMATION_CODE)
  }

}
