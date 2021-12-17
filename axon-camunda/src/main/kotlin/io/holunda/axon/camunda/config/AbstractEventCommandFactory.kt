package io.holunda.axon.camunda.config

/**
 * Base class for implementing process specific factory for mapping between Axon and Camunda concepts.
 */
abstract class AbstractEventCommandFactory(
  val processDefinitionKey: String
) : CamundaAxonCommandFactory, CamundaAxonEventFactory
