package io.holunda.axon.camunda.example.travel.compensation.process.delegate

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class Logger : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {
    logger.info { "Executed in ${execution.activityInstanceId}" }
    execution.variables.forEach { name, value -> logger.info { "\t$name : '$value'" } }
  }
}


