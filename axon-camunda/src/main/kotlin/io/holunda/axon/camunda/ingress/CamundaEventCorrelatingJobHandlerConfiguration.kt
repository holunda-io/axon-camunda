package io.holunda.axon.camunda.ingress

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration

data class CamundaEventCorrelatingJobHandlerConfiguration(
  val processDefinitionKey: String,
  val eventName: String,
  val local: Boolean = false,
  val variables: Map<String, Any> = mapOf(),
  val businessKey: String? = null,
  val correlationKeys: Map<String, Any> = mapOf(),
  val startProcess: Boolean = false
) : JobHandlerConfiguration {

  companion object {

    fun fromCanonicalString(objectMapper: ObjectMapper = jacksonObjectMapper(), value: String): CamundaEventCorrelatingJobHandlerConfiguration {
      return objectMapper.readValue(value)
    }
  }

  override fun toCanonicalString(): String = throw UnsupportedOperationException()
}
