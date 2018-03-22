package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventListener
import org.axonframework.eventhandling.EventMessage
import org.camunda.bpm.engine.RuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component

@Configuration
@ComponentScan
class AxonConfiguration(private val camundaEventHandler: CamundaEventHandler) {

  @Autowired
  fun configure(eventHandlingConfiguration: EventHandlingConfiguration) {
    eventHandlingConfiguration.registerEventHandler { _ -> camundaEventHandler }
  }
}

@Component
class CamundaEventHandler(private val runtime: RuntimeService, private val registry: CamundaAxonEventCommandFactoryRegistry) : EventListener {

  companion object: KLogging()

  override fun handle(event: EventMessage<*>) {
    logger.info { "Handling event $event" }

    // iterate over all factories and map to signal event
    // fire every successful mapping as a BPMN signal
    registry.factories().map { it.event(event.payload, event.metaData) }.forEach { signalEvent ->
      signalEvent?.let {
        runtime.createSignalEvent(signalEvent.name).setVariables(signalEvent.variables.toMap()).send()
        logger.info { "Signaled event" }
      }
    }
  }
}

/**
 * Enables support for Axon Camunda event/command distribution.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention
@Import(value = arrayOf(AxonConfiguration::class))
annotation class EnableAxonCamunda
