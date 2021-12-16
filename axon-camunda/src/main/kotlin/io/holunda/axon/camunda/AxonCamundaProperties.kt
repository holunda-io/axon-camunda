package io.holunda.axon.camunda

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "axon.camunda")
@ConstructorBinding
data class AxonCamundaProperties(
  val throwSignals: Boolean = false,
  val throwMessages: Boolean = true
)
