package io.holunda.axon.camunda.config

/**
 * Unknown command. Given message in BPMN is
 */
class UnknownCommandException(messageName: String, processDefinitionKey: String) :
  Exception("Could not determine command for message $messageName. Please configure the command in the factory for $processDefinitionKey")
