package io.holunda.axon.camunda.example.travel.compensation.process.delegate

import io.holunda.axon.camunda.example.travel.compensation.process.MessageBasedTravelProcessWithCompensation
import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
open class RecordFlightCompensation : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {

    logger.info{"Compensating flight booking"}
    execution.removeVariable(MessageBasedTravelProcessWithCompensation.Variables.TICKET_NUMBER)
  }

}

@Component
open class RecordHotelCompensation : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {

    logger.info{"Compensating hotel booking"}
    execution.removeVariable(MessageBasedTravelProcessWithCompensation.Variables.HOTEL_CONFIRMATION_CODE)
  }

}
