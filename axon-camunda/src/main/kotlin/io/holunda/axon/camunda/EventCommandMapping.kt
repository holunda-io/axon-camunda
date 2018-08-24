package io.holunda.axon.camunda

import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution


/**
 * Factory providing Camunda BPMN signals and messages (name and payload) for a received Axon Event.
 */
interface CamundaAxonEventFactory {
  /**
   * Maps Axon Event to Camunda event.
   * @return null, if no mapping defined
   */
  fun event(payload: Any, metadata: MetaData): CamundaEvent? = null

}

/**
 * Factory providing Axon commands for given BPMN throwing message signal name and a process execution.
 */
interface CamundaAxonCommandFactory {

  /**
   * Maps a message from BPMN to a Command sent to Aggregate.
   */
  fun command(messageName: String, execution: DelegateExecution): Any {
    throw UnknownCommandException()
  }

  /**
   * Maps an Axon error thrown by the Aggregate to a BPMN error relevant for the execution.
   */
  fun error(cause: Throwable): BpmnError? = null

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


