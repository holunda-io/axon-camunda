package io.holunda.axon.camunda.config


/**
 * Wrapper of a BPMN signal containing name and payload.
 */
data class CamundaEvent(
  val name: String,
  val variables: Map<String, Any>,
  val correlationVariableName: String? = null
)


