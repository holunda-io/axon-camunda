package io.holunda.axon.camunda.config

import mu.KLogging
import org.springframework.stereotype.Component

/**
 * Registry to get access to process-specific factories.
 */
@Component
class CamundaAxonEventCommandFactoryRegistry {

  companion object : KLogging()

  private val factories: MutableMap<String, AbstractEventCommandFactory> = mutableMapOf()

  /**
   * Registers a factory for resolution.
   */
  fun register(factory: AbstractEventCommandFactory) {
    if (factories.containsKey(factory.processDefinitionKey)) {
      logger.warn { "The registry already contains a factory for ${factory.processDefinitionKey}. Replacing a factory with a new one." }
    }
    factories[factory.processDefinitionKey] = factory
  }

  /**
   * Retrieves a factory for the process definition key.
   */
  fun getFactory(processDefinitionKey: String): AbstractEventCommandFactory =
    if (factories.containsKey(processDefinitionKey)) {
      factories.getValue(processDefinitionKey)
    } else {
      throw IllegalArgumentException("No factory for process definition key $processDefinitionKey found.")
    }

  /**
   * List all process definitions.
   */
  fun processDefinitions() = factories.keys
}
