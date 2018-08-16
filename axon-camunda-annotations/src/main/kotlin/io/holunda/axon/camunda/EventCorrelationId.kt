package io.holunda.axon.camunda

/**
 * Marks a field or method to be a correlation of id of the event.
 */
@MustBeDocumented
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FIELD,
  AnnotationTarget.FUNCTION)
@Retention
annotation class EventCorrelationId
