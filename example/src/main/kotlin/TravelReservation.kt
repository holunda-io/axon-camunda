package io.holunda.axon.camunda.example

import io.holunda.axon.camunda.AbstractEventCommandFactory
import io.holunda.axon.camunda.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.CamundaSignalEvent
import io.holunda.axon.camunda.DefaultSmartLifecycle
import io.holunda.axon.camunda.example.flight.CreateFlight
import io.holunda.axon.camunda.example.hotel.BookHotel
import io.holunda.axon.camunda.example.hotel.CreateHotel
import io.holunda.axon.camunda.example.hotel.HotelBooked
import mu.KLogging
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
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Process constants
 */
object TravelProcess {
  const val KEY = "travel"
  const val BPMN = "travel.bpmn"

  object Variables {
    const val RESERVATION = "reservation"
    const val HOTEL_RESERVATION_ID = "hotelReservationId"
  }

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
  }

  object Signals {
      const val HOTEL_BOOKED = "hotelBooked"
  }
}

@Configuration
class TravelReservationConfiguration(private val gateway: CommandGateway) {

  @Bean
  fun init() = object : DefaultSmartLifecycle(1000) {
    override fun onStart() {

      val now = LocalDateTime.now();

      gateway.send<Any>(CreateHotel("astoria", "Hamburg"))
      gateway.send<Any>(CreateFlight("LH-123", now, now.plusHours(2), "HAM", "MUC", 10))
      gateway.send<Any>(CreateFlight("LH-124", now.plusHours(8), now.plusHours(10), "HAM", "MUC", 10))
    }
  }

  companion object: KLogging()

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
          is HotelBooked -> {
            // return hotel reservation id into the process payload
            CamundaSignalEvent(TravelProcess.Signals.HOTEL_BOOKED, mapOf<String, Any>(TravelProcess.Variables.HOTEL_RESERVATION_ID to payload.reservationId))
          }
          else -> null
        }

    })
  }
}

@RestController
class ProcessStarter(private val runtime: RuntimeService) {

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

