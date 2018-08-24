package io.holunda.axon.camunda

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition
import org.camunda.bpm.model.bpmn.instance.ThrowEvent

object ELEMENTTEMPLATES {
  const val messageAttribute = "message"
}
/**
 * Extension function for process execution to extract message name.
 */
fun DelegateExecution.messageName(): String {
  // rebind to allow smart cast
  val modelInstance = bpmnModelElementInstance

  // try the message throw event
  if (modelInstance is ThrowEvent) {
    val eventDefinition = modelInstance.eventDefinitions.iterator().next()
    if (eventDefinition is MessageEventDefinition) {
      return eventDefinition.message.name.trim()
    }
  }

  // try extension element
  val property = modelInstance.domElement.childElements.find { it.localName == "extensionElements" }
    ?.childElements?.find { it.localName == "properties" }
    ?.childElements?.find { it.localName == "property" && it.hasAttribute("name") && it.getAttribute("name") == ELEMENTTEMPLATES.messageAttribute }
    ?.getAttribute("value")
    ?.trim()
  if (property != null && !property.isEmpty()) {
    return property
  }

  throw IllegalArgumentException("Message must be set (use BPMN throw event or Element Template for Axon Command).")
}



/**
 * Extension function for process execution to extract process definition key.
 */
fun DelegateExecution.processDefinitionKey(): String {
  return processDefinitionId.split(":")[0]
}
