package io.holunda.axon.camunda

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "axon.camunda")
data class AxonCamundaProperties(
  var sendSignals: Boolean = false
)
