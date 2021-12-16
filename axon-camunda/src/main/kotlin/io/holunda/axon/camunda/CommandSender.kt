package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException

/**
 * Delegate to send command to Axon.
 */
@Component
class CommandSender(
  private val registry: CamundaAxonEventCommandFactoryRegistry, private val gateway: CommandGateway
) : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {
    sendCommand(gateway, execution, registry, execution.messageName())
  }
}

@Component
class MessageCommandSender(
  private val registry: CamundaAxonEventCommandFactoryRegistry, private val gateway: CommandGateway
) {
  fun send(messageName: String, execution: DelegateExecution) {
    sendCommand(gateway, execution, registry, messageName)
  }
}


internal fun sendCommand(gateway: CommandGateway, execution: DelegateExecution, registry: CamundaAxonEventCommandFactoryRegistry, messageName: String) {
  val factory = registry.commandFactory(execution.processDefinitionKey())
  val command = factory.command(messageName, execution)
  CommandSender.logger.info { "Sending command: $command" }
  val result = gateway.send<Any>(command)
  try {
    result.get()
  } catch (e: ExecutionException) {
    val error = factory.error(e.cause!!) ?: e.cause!!
    CommandSender.logger.error { "Error sending command $messageName. Throwing $error." }
    throw error
  }
}
