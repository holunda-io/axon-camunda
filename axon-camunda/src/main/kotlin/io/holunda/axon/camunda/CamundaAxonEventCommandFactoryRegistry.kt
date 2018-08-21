package io.holunda.axon.camunda

import org.springframework.stereotype.Component

/**
 * Registry to get access to process-specific factories.
 */
@Component
class CamundaAxonEventCommandFactoryRegistry {

  private val factories: MutableMap<String, AbstractEventCommandFactory> = mutableMapOf()

  fun register(factory: AbstractEventCommandFactory) {
    factories.put(factory.processDefinitionKey, factory)
  }

  fun commandFactory(processDefinitionKey: String): AbstractEventCommandFactory =
    if (factories.containsKey(processDefinitionKey)) {
      factories[processDefinitionKey]!!
    } else {
      throw IllegalArgumentException("No factory for process definition key $processDefinitionKey found.")
    }

  fun factories() = factories.values

  fun processDefinitionKeys() = factories.keys
}
