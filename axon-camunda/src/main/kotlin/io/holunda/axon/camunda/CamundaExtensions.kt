package io.holunda.axon.camunda

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition
import org.camunda.bpm.model.bpmn.instance.ThrowEvent

/**
 * Extension function for delegate execution to extract message name.
 */
fun DelegateExecution.messageName(): String {
  // rebind to allow smart cast
  val modelInstance = bpmnModelElementInstance
  if (modelInstance is ThrowEvent) {
    val eventDefinition = modelInstance.eventDefinitions.iterator().next()
    if (eventDefinition is MessageEventDefinition) {
      return eventDefinition.message.name
    }
  }
  throw IllegalArgumentException("Message sending is supported using BPMN throwing message events only.")
}

/**
 * Extension function for delegate execution to extract process definition key.
 */
fun DelegateExecution.processDefinitionKey(): String {
  return processDefinitionId.split(":")[0]
}
