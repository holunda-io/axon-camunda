package io.holunda.axon.camunda.ingress

import mu.KLogging
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.springframework.stereotype.Component

@Component
class AxonCamundaEnginePlugin : AbstractProcessEnginePlugin() {

  companion object: KLogging()

  override fun preInit(processEngineConfiguration: ProcessEngineConfigurationImpl) {
    logger.info { "[AXON-CAMUNDA-001]: Axon camunda plugin initialized."}
    processEngineConfiguration.customJobHandlers = (processEngineConfiguration.customJobHandlers ?: mutableListOf()) + CamundaEventCorrelatingJobHandler()
  }
}
