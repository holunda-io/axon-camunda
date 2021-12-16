package io.holunda.axon.camunda

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

/**
 * Delegate to send command to Axon.
 */
@Component("commandSender")
class CommandSenderDelegate(
  private val sender: CamundaCommandSender,
) : JavaDelegate {

  override fun execute(execution: DelegateExecution) {
    sender.sendCommand(execution, execution.messageName())
  }
}


