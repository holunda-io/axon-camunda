package io.holunda.axon.camunda

import io.holunda.axon.camunda.job.CamundaEventCorrelatingJobHandler.Companion.TYPE
import io.holunda.axon.camunda.job.CamundaEventCorrelatingJobHandlerConfiguration
import mu.KLogging
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.EventMessageHandler
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity
import org.springframework.stereotype.Component

@Component
class CamundaEventMessageHandler(
  private val runtime: RuntimeService,
  private val registry: CamundaAxonEventCommandFactoryRegistry,
  private val axonCamundaProperties: AxonCamundaProperties,
  private val processEngineConfigurationImpl: ProcessEngineConfigurationImpl
) : EventMessageHandler {

  companion object : KLogging()

  override fun handle(event: EventMessage<*>) {
    // iterate over all factories and map to signal | message
    // fire every successful mapping as a BPMN signal

    logger.info { "Received Axon event message with payload of type ${event.payloadType.name}" }

    registry
      .processDefinitions()
      .map { processDefinitionKey -> processDefinitionKey to registry.commandFactory(processDefinitionKey) }
      .map { (processDefinitionKey, factory) -> processDefinitionKey to factory.event(event.payload, event.metaData) }
      .forEach { (processDefinitionKey, camundaEvent) ->
        camundaEvent?.let { camundaEvent ->

          if (axonCamundaProperties.sendSignals) {
            // signals for all
            logger.info { "Throwing signal ${camundaEvent.name}" }
            runtime.signalEventReceived(camundaEvent.name, camundaEvent.variables.toMap())
          }

          // messages for subscribed processes
          val correlationId = extractCorrelationId(event.payload) ?: return

          // check manually if correlation variable is matching the process definition key of the factory

          if (camundaEvent.correlationVariableName != null) {

            processEngineConfigurationImpl.commandExecutorTxRequired.execute {
              it.jobManager.send(
                MessageEntity()
                  .apply {
                    this.jobHandlerConfiguration = CamundaEventCorrelatingJobHandlerConfiguration(
                      processDefinitionKey = processDefinitionKey,
                      correlationId = correlationId,
                      eventName = camundaEvent.name,
                      correlationVariableName = camundaEvent.correlationVariableName,
                      variables = camundaEvent.variables
                    )
                    this.jobHandlerType = TYPE
                  }
              )
            }
          }
        }
      }
  }
}

