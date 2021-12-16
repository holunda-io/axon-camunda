package io.holunda.axon.camunda

import org.axonframework.commandhandling.AsynchronousCommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.monitoring.NoOpMessageMonitor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.concurrent.Executors

@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableConfigurationProperties(AxonCamundaProperties::class)
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
