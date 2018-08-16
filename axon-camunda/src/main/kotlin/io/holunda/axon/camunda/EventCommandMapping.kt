package io.holunda.axon.camunda

import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition
import org.camunda.bpm.model.bpmn.instance.ThrowEvent


/**
 * Factory providing Camunda BPMN signals and messages (name and payload) for a received Axon Event.
 */
interface CamundaAxonEventFactory {
  fun event(payload: Any, metadata: MetaData): CamundaEvent? = null
}

/**
 * Factory providing Axon commands for given BPMN throwing message signal name and a delegate execution.
 */
interface CamundaAxonCommandFactory {
  fun command(messageName: String, execution: DelegateExecution): Any {
    throw UnknownCommandException()
  }
}

/**
 * Base class for implementing process specific factory for mapping between Axon and Camunda concepts.
 */
abstract class AbstractEventCommandFactory(val processDefinitionKey: String) : CamundaAxonCommandFactory, CamundaAxonEventFactory


/**
 * Wrapper of a BPMN signal contaning name and payload.
 */
data class CamundaEvent(
  val name: String,
  val variables: Map<String, Any>,
  val correlationVariableName: String? = null
)


/**
 * Unknown command.
 */
class UnknownCommandException : Exception()


