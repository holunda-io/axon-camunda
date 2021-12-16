package io.holunda.axon.camunda.config

import org.axonframework.messaging.MetaData

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
