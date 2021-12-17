package io.holunda.axon.camunda.ingress

import io.holunda.axon.camunda.ingress.CamundaEventCorrelatingJobHandlerConfiguration.Companion.fromCanonicalString
import mu.KLogging
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity

/**
 * Job handler for asynchronous delivery of events (as a separate job).
 */
class CamundaEventCorrelatingJobHandler : JobHandler<CamundaEventCorrelatingJobHandlerConfiguration> {

  companion object : KLogging() {
    const val TYPE = "axon-event-correlation"
  }

  init {
    logger.info { "AXON-CAMUNDA-002: Axon Events will be delivered to Camunda." }
  }

  override fun getType(): String = TYPE

  override fun execute(
    configuration: CamundaEventCorrelatingJobHandlerConfiguration,
    execution: ExecutionEntity?,
    commandContext: CommandContext,
    tenantId: String?
  ) {
    val runtimeService = commandContext.processEngineConfiguration.runtimeService

    if (configuration.correlationId != null && configuration.correlationVariableName != null) {

      val instances = runtimeService
        .createProcessInstanceQuery()
        .processDefinitionKey(configuration.processDefinitionKey)
        .variableValueEquals(configuration.correlationVariableName, configuration.correlationId)
        .list()
      if (instances.size == 1) {
        logger.debug { "Correlation id found ${configuration.correlationId}, correlating ${configuration.eventName} message using it with instance of ${configuration.processDefinitionKey}." }
        runtimeService
          .createMessageCorrelation(configuration.eventName)
          .processInstanceVariablesEqual(mutableMapOf<String, Any>(configuration.correlationVariableName to configuration.correlationId))
          .let {
            if (configuration.local) {
              it.setVariablesLocal(configuration.variables)
            } else {
              it.setVariables(configuration.variables)
            }
          }
          .correlate()
      } else {
        logger.warn { "Skipping correlation of ${configuration.eventName}: id ${configuration.correlationId}, found ${instances.size} instances for ${configuration.processDefinitionKey} instead of 1." }
      }
    } else {

      val subscriptions = runtimeService
        .createEventSubscriptionQuery()
        .eventName(configuration.eventName)
        .eventType("signal")
        .list()

      if (subscriptions.size > 0) {

        runtimeService
          .createSignalEvent(configuration.eventName)
          .setVariables(configuration.variables)
          .send()
      } else {
        logger.warn { "No subscription found for signal ${configuration.eventName}" }
      }
    }
  }

  override fun newConfiguration(canonicalString: String) = fromCanonicalString(canonicalString)


  override fun onDelete(configuration: CamundaEventCorrelatingJobHandlerConfiguration, jobEntity: JobEntity) {

  }
}
