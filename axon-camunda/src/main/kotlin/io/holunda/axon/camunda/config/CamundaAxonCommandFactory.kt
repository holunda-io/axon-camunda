package io.holunda.axon.camunda.config

import io.holunda.axon.camunda.processDefinitionKey
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution

/**
 * Factory providing Axon commands for given BPMN throwing message signal name and a process execution.
 */
interface CamundaAxonCommandFactory {

  /**
   * Maps a message from BPMN to a Command sent to Aggregate.
   */
  fun command(messageName: String, execution: DelegateExecution): Any {
    throw UnknownCommandException(messageName = messageName, processDefinitionKey = execution.processDefinitionKey())
  }

  /**
   * Maps an Axon error thrown by the Aggregate to a BPMN error relevant for the execution.
   */
  fun error(cause: Throwable): BpmnError? = null

}
