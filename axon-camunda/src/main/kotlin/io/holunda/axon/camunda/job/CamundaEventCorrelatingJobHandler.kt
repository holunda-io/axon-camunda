package io.holunda.axon.camunda.job

import mu.KLogging
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.holunda.axon.camunda.job.CamundaEventCorrelatingJobHandlerConfiguration.Companion.fromCanonicalString
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity

class CamundaEventCorrelatingJobHandler(
) : JobHandler<CamundaEventCorrelatingJobHandlerConfiguration> {

  companion object: KLogging() {
    const val TYPE = "AxonEventCorrelation"
  }

  override fun getType(): String = TYPE

  override fun execute(
    configuration: CamundaEventCorrelatingJobHandlerConfiguration,
    execution: ExecutionEntity?,
    commandContext: CommandContext,
    tenantId: String?
  ) {

    val runtime = commandContext.processEngineConfiguration.runtimeService
    val processDefinitionKey = configuration.processDefinitionKey
    val correlationId = configuration.correlationId
    val correlationVariableName = configuration.correlationVariableName
    val eventName = configuration.eventName
    val variables = configuration.variables

    if (runtime
        .createProcessInstanceQuery()
        .processDefinitionKey(processDefinitionKey)
        .variableValueEquals(correlationVariableName, correlationId)
        .count() > 0) {
      logger.info { "Correlation id found $correlationId, correlating a message using it with instance of $processDefinitionKey." }

      runtime
        .createMessageCorrelation(eventName)
        .processInstanceVariablesEqual(mutableMapOf<String, Any>(correlationVariableName to correlationId))
        .setVariables(variables)
        .correlate()

    } else {
      logger.info { "Skipping correlation of id $correlationId, no instances found for $processDefinitionKey" }
    }

  }

  override fun newConfiguration(canonicalString: String) = fromCanonicalString(canonicalString)


  override fun onDelete(configuration: CamundaEventCorrelatingJobHandlerConfiguration, jobEntity: JobEntity) {

  }
}
