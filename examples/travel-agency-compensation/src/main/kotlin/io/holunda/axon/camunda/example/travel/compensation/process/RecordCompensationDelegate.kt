package io.holunda.axon.camunda.example.travel.compensation.process

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
open class RecordCompensation : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {

    logger.info{"Compensating hotel booking"}
    execution.removeVariable(MessageBasedTravelProcessWithCompensation.Variables.HOTEL_CONFIRMATION_CODE)
  }

}
