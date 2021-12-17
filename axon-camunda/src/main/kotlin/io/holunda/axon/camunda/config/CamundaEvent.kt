package io.holunda.axon.camunda.config


/**
 * Wrapper of a BPMN signal containing name and payload.
 */
data class CamundaEvent(
  /**
   * Name of the event, as defined in BPMN.
   */
  val name: String,
  /**
   * List of variables to set during event delivery.
   */
  val variables: Map<String, Any>,
  /**
   * Correlation scope.
   */
  val local: Boolean = false,
  /**
   * Name of the variable to identify the execution. If omitted, no message correlation will be executed (but signal might be still executed).
   */
  val correlationVariableName: String? = null
)


