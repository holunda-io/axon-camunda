package io.holunda.axon.camunda

import org.axonframework.config.Configurer
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableConfigurationProperties(AxonCamundaProperties::class)
class AxonCamundaConfiguration {

  @Autowired
  fun configure(axonConfiguration: Configurer, camundaEventHandler: CamundaEventHandler) {
    axonConfiguration.registerEventHandler { camundaEventHandler }
  }

  @Bean
  fun eventStorageEngine() = InMemoryEventStorageEngine()

  //@Bean   FIXME noch nötig?
  //fun commandBus(txManager: TransactionManager) = AsynchronousCommandBus(Executors.newCachedThreadPool(), txManager, NoOpMessageMonitor.INSTANCE)

}

/**
 * Enables support for Axon Camunda signal/command distribution.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention
@Import(value = arrayOf(AxonCamundaConfiguration::class))
annotation class EnableAxonCamunda
