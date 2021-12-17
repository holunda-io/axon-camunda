package io.holunda.axon.camunda

import io.holunda.axon.camunda.ingress.CamundaEventMessageHandler
import org.axonframework.config.Configurer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(AxonCamundaProperties::class)
@ComponentScan
class AxonCamundaConfiguration {

  @Autowired
  fun configure(
    axonConfiguration: Configurer, handler: CamundaEventMessageHandler
  ) {
    axonConfiguration.registerEventHandler { handler }
  }
}

