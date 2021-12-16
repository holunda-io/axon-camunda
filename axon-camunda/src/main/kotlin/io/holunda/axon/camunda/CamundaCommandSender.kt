package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException

@Component
class CamundaCommandSender(
  private val gateway: CommandGateway,
  private val registry: CamundaAxonEventCommandFactoryRegistry
) {

  companion object: KLogging()

  fun sendCommand(execution: DelegateExecution, messageName: String) {

    val factory = registry.commandFactory(execution.processDefinitionKey())
    val command = factory.command(messageName, execution)

    logger.info { "Sending command: $command" }
    val result = gateway.send<Any>(command)
    try {
      result.get()
    } catch (e: ExecutionException) {
      val error = factory.error(e.cause!!) ?: e.cause!!
      logger.error { "Error sending command $messageName. Throwing $error." }
      throw error
    }
  }


}
