package io.holunda.axon.camunda.config

/**
 * Unknown command.
 */
class UnknownCommandException(messageName: String) : Exception("Could not determine command for message $messageName")
