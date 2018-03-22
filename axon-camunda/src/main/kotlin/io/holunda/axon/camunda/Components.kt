package io.holunda.axon.camunda

import org.axonframework.commandhandling.gateway.CommandGateway
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

/**
 * Delegate to send command to Axon.
 */
@Component
class CommandSender(private val registry: CamundaAxonEventCommandFactoryRegistry, private val gateway: CommandGateway): JavaDelegate {

  override fun execute(execution: DelegateExecution) {
    gateway.send<Any>(registry.commandFactory(execution.processDefinitionKey()).command(execution.messageName(), execution))
  }
}
