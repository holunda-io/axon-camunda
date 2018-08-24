package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

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


fun sendCommand(gateway: CommandGateway, execution: DelegateExecution, registry: CamundaAxonEventCommandFactoryRegistry, messageName: String) {
  val factory = registry.commandFactory(execution.processDefinitionKey())
  gateway.send<Any, Any?>(factory.command(messageName, execution),
    object : CommandCallback<Any, Any?> {
      override fun onSuccess(commandMessage: CommandMessage<out Any>, result: Any?) {
        CommandSender.logger.debug { "Successfully sent command message $commandMessage" }
      }

      override fun onFailure(commandMessage: CommandMessage<out Any>, cause: Throwable) {
        val error = factory.error(cause) ?: cause
        CommandSender.logger.error { "Error sending command $commandMessage. Throwing $error." }
        throw error
      }
    })
}
