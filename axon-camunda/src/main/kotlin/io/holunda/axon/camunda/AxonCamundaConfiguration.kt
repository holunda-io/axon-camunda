package io.holunda.axon.camunda

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.ingress.CamundaEventCorrelatingJobHandler
import io.holunda.axon.camunda.ingress.CamundaEventMessageHandler
import org.axonframework.config.Configurer
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity
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

fun ProcessEngineConfigurationImpl.createCustomJob(configuration: JobHandlerConfiguration, objectMapper: ObjectMapper): Unit = this.commandExecutorTxRequired.execute { context ->
  context.jobManager.send(
    MessageEntity()
      .apply {
        this.jobHandlerConfigurationRaw = objectMapper.writeValueAsString(configuration)
        this.jobHandlerType = CamundaEventCorrelatingJobHandler.TYPE
      }
  )
}

