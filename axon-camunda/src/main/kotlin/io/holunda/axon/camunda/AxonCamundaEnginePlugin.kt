package io.holunda.axon.camunda

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.ingress.CamundaEventCorrelatingJobHandler
import mu.KLogging
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.springframework.stereotype.Component

@Component
class AxonCamundaEnginePlugin(
  val objectMapper: ObjectMapper
) : AbstractProcessEnginePlugin() {

  companion object : KLogging()

  override fun preInit(processEngineConfiguration: ProcessEngineConfigurationImpl) {
    logger.info { "AXON-CAMUNDA-001: Axon Camunda Plugin initialized." }
    processEngineConfiguration.customJobHandlers =
      (processEngineConfiguration.customJobHandlers ?: mutableListOf()) + CamundaEventCorrelatingJobHandler(objectMapper)
  }
}
