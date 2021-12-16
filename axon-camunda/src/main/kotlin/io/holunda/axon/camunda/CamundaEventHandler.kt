package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.EventMessageHandler
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Component

@Component
class CamundaEventHandler(
  private val runtime: RuntimeService,
  private val registry: CamundaAxonEventCommandFactoryRegistry,
  private val axonCamundaProperties: AxonCamundaProperties) : EventMessageHandler {

  companion object : KLogging()

  override fun handle(event: EventMessage<*>) {
    // iterate over all factories and map to signal | message
    // fire every successful mapping as a BPMN signal

    logger.info { "Received Axon event message with payload of type ${event.payloadType.name}" }

    registry
      .processDefinitions()
      .map { processDefinitionKey -> processDefinitionKey to registry.commandFactory(processDefinitionKey)  }
      .map { (processDefinitionKey, factory) -> processDefinitionKey to factory.event(event.payload, event.metaData) }
      .forEach { (processDefinitionKey, camundaEvent) ->
        camundaEvent?.let {

          if (axonCamundaProperties.sendSignals) {
            // signals for all
            logger.info { "Throwing signal ${camundaEvent.name}" }
            runtime.signalEventReceived(camundaEvent.name, camundaEvent.variables.toMap())
          }

          // messages for subscribed processes
          val correlationId = extractCorrelationId(event.payload) ?: return

          // check manually if correlation variable is matching the process definition key of the factory

          if (camundaEvent.correlationVariableName != null) {

            if (runtime
                .createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .variableValueEquals(camundaEvent.correlationVariableName, correlationId)
                .count() > 0) {
              logger.info { "Correlation id found $correlationId, correlating a message using it with instance of $processDefinitionKey." }
              runtime
                .createMessageCorrelation(camundaEvent.name)
                .processInstanceVariablesEqual(mutableMapOf<String, Any>(camundaEvent.correlationVariableName to correlationId))
                .setVariables(camundaEvent.variables.toMap())
                .correlate()

            } else {
              logger.info { "Skipping correlation of id $correlationId, no instances found for $processDefinitionKey" }
            }

          }
        }
      }
  }
}

