package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.eventhandling.EventListener
import org.axonframework.eventhandling.EventMessage
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Component

@Component
class CamundaEventHandler(
  private val runtime: RuntimeService,
  private val registry: CamundaAxonEventCommandFactoryRegistry,
  private val axonCamundaProperties: AxonCamundaProperties) : EventListener {

  companion object : KLogging()

  override fun handle(event: EventMessage<*>) {
    // iterate over all factories and map to signal signal
    // fire every successful mapping as a BPMN signal
    registry.factories()
      .map { factory -> factory.event(event.payload, event.metaData) }
      .forEach { camundaEvent ->
        camundaEvent?.let {

          if (axonCamundaProperties.sendSignals) {
            // signals for all
            logger.info { "Throwing signal ${camundaEvent.name}" }
            runtime.signalEventReceived(camundaEvent.name, camundaEvent.variables.toMap())
          }

          // messages for subscribed processes
          val correlationId = extractCorrelationId(event.payload) ?: return

          if (camundaEvent.correlationVariableName != null) {
            logger.info { "Correlation id found $correlationId, correlating a message using it." }
            runtime.correlateMessage(camundaEvent.name, mutableMapOf<String, Any>(camundaEvent.correlationVariableName to correlationId), camundaEvent.variables.toMap())
          }
        }
      }
  }
}

