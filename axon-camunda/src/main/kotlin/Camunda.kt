package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventListener
import org.axonframework.eventhandling.EventMessage
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition
import org.camunda.bpm.model.bpmn.instance.ThrowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
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
 * Delegate to send command to Axon.
 */
@Component
class CommandSender: JavaDelegate {

  @Autowired
  lateinit var registry: CamundaAxonEventCommandFactoryRegistry

  @Autowired
  lateinit var gateway: CommandGateway

  override fun execute(execution: DelegateExecution) {
    registry.commandFactory(execution.processDefinitionKey())?.let {
      gateway.send<Any>(it.command(execution.messageName(), execution))
    }
  }
}

@Configuration
@ComponentScan
class CamundaAxonConfiguration {

  @Autowired
  lateinit var camundaEventHandler: CamundaEventHandler

  @Autowired
  fun configure(eventHandlingConfiguration: EventHandlingConfiguration) {
    eventHandlingConfiguration.registerEventHandler { eventHandlingConfiguration -> camundaEventHandler }
  }

}


@Component
class CamundaEventHandler : EventListener {

  @Autowired
  lateinit var runtime: RuntimeService

  @Autowired
  lateinit var registry: CamundaAxonEventCommandFactoryRegistry

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
 * Opinionated best guess implementation of [SmartLifecycle], just override [onStart] and  [getPhase]
 * to get an auto-started, startable and stoppable lifecycle manager.
 */
abstract class DefaultSmartLifecycle(val thePhase : Int) : SmartLifecycle {

  private var running: Boolean = false

  override fun start() {
    onStart()
    this.running = true
  }

  abstract fun onStart()

  override fun isAutoStartup() = true

  override fun stop(callback: Runnable?) {
    callback?.run()
    this.running = false
  }

  override fun stop() = stop(null)

  override fun isRunning() = running

  override fun getPhase(): Int = thePhase
}

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


