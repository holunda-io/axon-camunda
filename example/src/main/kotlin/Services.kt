package de.holisticon.axon.camunda.example

import de.holisticon.axon.camunda.example.hotel.BookHotel
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDate

@Component
class LoggingService: JavaDelegate {

  companion object: KLogging()

  override fun execute(execution: DelegateExecution) {
    logger.info{"Executed in ${execution.activityInstanceId}"}
  }
}
