package de.holisticon.axon.camunda.example

import de.holisticon.axon.camunda.example.hotel.BookHotel
import de.holisticon.axon.camunda.example.hotel.CreateHotel
import de.holisticon.axon.camunda.example.hotel.HotelBooked
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.threeten.extra.Interval
import java.io.Serializable
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Process constants
 */
object TravelProcess {
  const val KEY = "travel"
  const val BPMN = "travel.bpmn"

  object Variables {
    const val RESERVATION = "reservation"
  }

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
  }
}

@Configuration
class TravelReservationConfiguration {


  @Autowired
  lateinit var gateway: CommandGateway

  @Bean
  fun init() = object : DefaultSmartLifecycle(1000) {
    override fun onStart() {
      gateway.send<Any>(CreateHotel("astoria"))
    }
  }

  @Autowired
  fun configure(registry: CamundaAxonEventCommandFactoryRegistry) {

    registry.register(object: AbstractEventCommandFactory(TravelProcess.KEY) {

      /**
       * Commands
       */
      override fun command(messageName: String, execution: DelegateExecution): Any {
        val reservation = execution.getVariable(TravelProcess.Variables.RESERVATION) as Reservation

        // create a mapper between payload variables and command objects based on the message name
        return when (messageName) {
          TravelProcess.Messages.BOOK_HOTEL -> BookHotel(arrival = reservation.from, departure = reservation.to, guestName = reservation.name, hotelName = reservation.hotel)
          else -> super.command(messageName, execution)
        }
      }

      /**
       * Events
       */
      override fun event(payload: Any, metadata: MetaData): CamundaSignalEvent? =
        when (payload) {
          is HotelBooked -> CamundaSignalEvent("hotelBooked", mapOf<String, Any>("guestName" to ""))
          else -> null
        }

    })
  }
}

@Component
@RestController
class ProcessStarter {

  @Autowired
  lateinit var runtime: RuntimeService

  @PostMapping("/travel")
  fun startTravel(): ResponseEntity<String> {
    return ResponseEntity.ok(runtime.startProcessInstanceByKey(TravelProcess.KEY).processInstanceId)
  }

}

/**
 * Booking preparation service
 */
@Component
class BookingPreparationService : JavaDelegate {
  override fun execute(execution: DelegateExecution) {
    execution.setVariable(TravelProcess.Variables.RESERVATION,
      Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "astoria")
    )
  }
}

data class Reservation(
  val name: String,
  val from: LocalDate,
  val to: LocalDate,
  val hotel: String
) : Serializable


fun interval(start: LocalDate, end: LocalDate) = Interval.of(start.atStartOfDay().toInstant(ZoneOffset.UTC), end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))

