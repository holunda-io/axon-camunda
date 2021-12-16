package io.holunda.axon.camunda.egress

import io.holunda.axon.camunda.config.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.processDefinitionKey
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

  internal fun sendCommand(execution: DelegateExecution, messageName: String): Any? {

    val factory = registry.getFactory(execution.processDefinitionKey())
    val command = factory.command(messageName, execution)

    logger.debug { "Sending command: $command" }

    return try {
      gateway.sendAndWait<Any?>(command)
    } catch (e: ExecutionException) {
      val innerCause = e.cause!!
      val error = factory.error(innerCause) ?: innerCause
      logger.error { "Error sending command $messageName. Throwing $error." }
      throw error
    }
  }


}
