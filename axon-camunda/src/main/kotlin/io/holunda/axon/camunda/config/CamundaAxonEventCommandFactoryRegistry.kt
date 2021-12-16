package io.holunda.axon.camunda.config

import org.springframework.stereotype.Component

/**
 * Registry to get access to process-specific factories.
 */
@Component
class CamundaAxonEventCommandFactoryRegistry {

  private val factories: MutableMap<String, AbstractEventCommandFactory> = mutableMapOf()

  fun register(factory: AbstractEventCommandFactory) {
    factories[factory.processDefinitionKey] = factory
  }

  fun getFactory(processDefinitionKey: String): AbstractEventCommandFactory =
    if (factories.containsKey(processDefinitionKey)) {
      factories.getValue(processDefinitionKey)
    } else {
      throw IllegalArgumentException("No factory for process definition key $processDefinitionKey found.")
    }

  fun processDefinitions() = factories.keys
}
