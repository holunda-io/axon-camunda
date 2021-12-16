package io.holunda.axon.camunda.process

import io.holunda.axon.camunda.egress.CamundaCommandSender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Component

@Component("messageCommandSender")
class MessageCommandSender(
  private val sender: CamundaCommandSender
) {
  fun send(messageName: String, execution: DelegateExecution) {
    sender.sendCommand(execution, messageName)
  }
}
