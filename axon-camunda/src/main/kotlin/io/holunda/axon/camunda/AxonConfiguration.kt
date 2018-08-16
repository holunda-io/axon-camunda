package io.holunda.axon.camunda

import mu.KLogging
import org.axonframework.commandhandling.AsynchronousCommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventListener
import org.axonframework.eventhandling.EventMessage
import org.axonframework.monitoring.NoOpMessageMonitor
import org.camunda.bpm.engine.RuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.concurrent.Executors

@Configuration
@ComponentScan
@EnableTransactionManagement
class AxonConfiguration {

  @Autowired
  fun configure(eventHandlingConfiguration: EventHandlingConfiguration, camundaEventHandler: CamundaEventHandler) {
    eventHandlingConfiguration.registerEventHandler { _ -> camundaEventHandler }
  }

  @Bean
  fun commandBus(txManager: TransactionManager) = AsynchronousCommandBus(Executors.newCachedThreadPool(), txManager, NoOpMessageMonitor.INSTANCE)

}


/**
 * Enables support for Axon Camunda signal/command distribution.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention
@Import(value = arrayOf(AxonConfiguration::class))
annotation class EnableAxonCamunda
