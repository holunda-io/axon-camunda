package io.holunda.axon.camunda.ingress

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration

data class CamundaEventCorrelatingJobHandlerConfiguration(
  val processDefinitionKey: String,
  val eventName: String,
  val variables: Map<String, Any>,
  val correlationVariableName: String?,
  val correlationId: Any?,
) : JobHandlerConfiguration {

  companion object {

    val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun fromCanonicalString(value: String): CamundaEventCorrelatingJobHandlerConfiguration {
      return objectMapper.readValue(value)
    }
  }

  override fun toCanonicalString(): String = objectMapper.writeValueAsString(this)
}
