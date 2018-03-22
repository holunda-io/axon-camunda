package io.holunda.axon.camunda

import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition
import org.camunda.bpm.model.bpmn.instance.ThrowEvent
import org.springframework.stereotype.Component

/**
 * Registry to get access to process-specific factories.
 */
@Component
class CamundaAxonEventCommandFactoryRegistry() {

  private val factories: MutableMap<String, AbstractEventCommandFactory> = mutableMapOf()

  fun register(factory: AbstractEventCommandFactory) {
    factories.put(factory.processDefinitionKey, factory)
  }

  fun commandFactory(processDefinitionKey: String): AbstractEventCommandFactory =
    if (factories.containsKey(processDefinitionKey)) {
      factories[processDefinitionKey]!!
    } else {
      throw IllegalArgumentException("No factory for process definition id " + processDefinitionKey + " found.")
    }

  fun factories() = factories.values
}


/**
 * Factory providing Axon commands for given BPMN throwing message event name and a delegate execution.
 */
interface CamundaAxonCommandFactory {
  fun command(messageName: String, execution: DelegateExecution): Any {
    throw UnknownCommandException()
  }
}

/**
 * Factory providing BPMN signal (name and payload) for a received Axon Event.
 */
interface CamundaAxonEventFactory {
  fun event(payload: Any, metadata: MetaData): CamundaSignalEvent? = null
}

/**
 * Base class for implementing process specific factory for mapping between Axon and Camunda concepts.
 */
abstract class AbstractEventCommandFactory(val processDefinitionKey: String) : CamundaAxonCommandFactory, CamundaAxonEventFactory {

}

/**
 * Wrapper of a BPMN signal contaning name and payload.
 */
data class CamundaSignalEvent(val name: String, val variables: Map<String, Any>)

/**
 * Unknown command.
 */
class UnknownCommandException: Exception()


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
