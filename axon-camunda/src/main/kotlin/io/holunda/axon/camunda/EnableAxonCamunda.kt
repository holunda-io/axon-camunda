package io.holunda.axon.camunda

import org.springframework.context.annotation.Import

/**
 * Enables support for Axon Camunda signal/command distribution.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention
@Import(value = [AxonCamundaConfiguration::class])
annotation class EnableAxonCamunda
