package io.holunda.axon.camunda.ingress

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.ingress.CamundaEventCorrelatingJobHandlerConfiguration.Companion.fromCanonicalString
import mu.KLogging
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity
import org.camunda.bpm.engine.runtime.ProcessInstance

/**
 * Job handler for asynchronous delivery of events (as a separate job).
 */
class CamundaEventCorrelatingJobHandler(
  private val objectMapper: ObjectMapper
) : JobHandler<CamundaEventCorrelatingJobHandlerConfiguration> {

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

    if (configuration.startProcess) {
      requireNotNull(configuration.businessKey)

      val subscriptions = runtimeService.createEventSubscriptionQuery()
        .eventName(configuration.eventName)
        .eventType("message")
        .list()

      logger.debug { "Found ${subscriptions.size} subscriptions for message ${configuration.eventName}" }

      if (subscriptions.size == 1) {
        logger.debug { "Starting process by message ${configuration.eventName} with businessKey ${configuration.businessKey} and variables ${configuration.variables}" }

        runtimeService.startProcessInstanceByMessage(
          configuration.eventName,
          configuration.businessKey,
          configuration.variables
        )
      } else {
        logger.warn { "Skipping correlation of ${configuration.eventName}: keys ${configuration.correlationKeys}, found ${subscriptions.size} subscriptions instead of 1." }
      }

    } else if (configuration.businessKey != null) {
      val instances = runtimeService
        .createProcessInstanceQuery()
        .processDefinitionKey(configuration.processDefinitionKey)
        .processInstanceBusinessKey(configuration.businessKey)
        .list()

      correlate(instances, configuration, runtimeService)
    } else if (configuration.correlationKeys.isNotEmpty()) {
      val query = runtimeService
        .createProcessInstanceQuery()
        .processDefinitionKey(configuration.processDefinitionKey)

      configuration.correlationKeys.forEach {
        query.variableValueEquals(it.key, it.value)
      }

      correlate(query.list(), configuration, runtimeService)
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

  private fun correlate(
    instances: MutableList<ProcessInstance>,
    configuration: CamundaEventCorrelatingJobHandlerConfiguration,
    runtimeService: RuntimeService
  ) {
    if (instances.size == 1) {
      logger.debug { "Correlation found: keys ${configuration.correlationKeys}, correlating ${configuration.eventName} message using it with instance of ${configuration.processDefinitionKey}." }
      runtimeService
        .createMessageCorrelation(configuration.eventName)
        .processInstanceVariablesEqual(configuration.correlationKeys)
        .let {
          if (configuration.local) {
            it.setVariablesLocal(configuration.variables)
          } else {
            it.setVariables(configuration.variables)
          }
        }
        .correlate()
    } else {
      logger.warn { "Skipping correlation of ${configuration.eventName}: keys ${configuration.correlationKeys}, found ${instances.size} instances for ${configuration.processDefinitionKey} instead of 1." }
    }
  }

  override fun newConfiguration(canonicalString: String): CamundaEventCorrelatingJobHandlerConfiguration =
    fromCanonicalString(objectMapper = objectMapper, value = canonicalString)


  override fun onDelete(configuration: CamundaEventCorrelatingJobHandlerConfiguration, jobEntity: JobEntity) {

  }
}
