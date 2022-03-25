package io.holunda.axon.camunda.ingress

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.AxonCamundaProperties
import io.holunda.axon.camunda.config.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.config.CamundaEvent
import io.holunda.axon.camunda.createCustomJob
import io.holunda.axon.camunda.extractCorrelationId
import mu.KLogging
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.EventMessageHandler
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.springframework.stereotype.Component

/**
 * Receives events from event bus and delivers them to the process via Camunda job.
 */
@Component
class CamundaEventMessageHandler(
  private val registry: CamundaAxonEventCommandFactoryRegistry,
  private val axonCamundaProperties: AxonCamundaProperties,
  private val processEngineConfigurationImpl: ProcessEngineConfigurationImpl,
  private val objectMapper: ObjectMapper
) : EventMessageHandler {

  companion object : KLogging()

  override fun handle(event: EventMessage<*>) {
    // iterate over all factories and map to signal | message
    // fire every successful mapping as a BPMN signal

    logger.debug { "Received Axon Event message with payload of type ${event.payloadType.name}" }

    registry
      .processDefinitions()
      .map { processDefinitionKey -> processDefinitionKey to registry.getFactory(processDefinitionKey).event(event.payload, event.metaData) }
      .forEach { (processDefinitionKey, camundaEvent) ->
        if (camundaEvent != null) {
          if (axonCamundaProperties.throwSignals) {
            throwSignalEvent(processDefinitionKey, camundaEvent)
          }
          if (axonCamundaProperties.throwMessages) {
            // extract correlation id to find the process instance
            val correlationId = extractCorrelationId(event.payload) ?: return
            throwMessageEvent(processDefinitionKey, correlationId, camundaEvent)
          }
        } else {
          logger.debug { "Could not map Axon Event for process definition $processDefinitionKey. Event is skipped." }
        }
      }
  }

  private fun throwSignalEvent(processDefinitionKey: String, camundaEvent: CamundaEvent) {
    createMessageJob(
      CamundaEventCorrelatingJobHandlerConfiguration(
        processDefinitionKey = processDefinitionKey,
        eventName = camundaEvent.name,
        variables = camundaEvent.variables,
        local = camundaEvent.local
      )
    )
  }

  private fun throwMessageEvent(processDefinitionKey: String, correlationId: Any, camundaEvent: CamundaEvent) {
    if (camundaEvent.correlationVariableName != null) {
      createMessageJob(
        CamundaEventCorrelatingJobHandlerConfiguration(
          processDefinitionKey = processDefinitionKey,
          eventName = camundaEvent.name,
          variables = camundaEvent.variables,
          local = camundaEvent.local,
          correlationKeys = mapOf(Pair(camundaEvent.correlationVariableName, correlationId))
        )
      )
    } else {
      logger.debug { "No variable is configured to find correlation. Skipping delivery." }
    }
  }

  private fun createMessageJob(configuration: CamundaEventCorrelatingJobHandlerConfiguration) {
    processEngineConfigurationImpl.createCustomJob(configuration, objectMapper)
  }
}

