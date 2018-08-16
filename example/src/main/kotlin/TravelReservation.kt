package io.holunda.axon.camunda.example

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.holunda.axon.camunda.AbstractEventCommandFactory
import io.holunda.axon.camunda.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.CamundaEvent
import DefaultSmartLifecycle
import io.holunda.axon.camunda.example.flight.BookFlight
import io.holunda.axon.camunda.example.flight.CreateFlight
import io.holunda.axon.camunda.example.flight.FlightBooked
import io.holunda.axon.camunda.example.hotel.BookHotel
import io.holunda.axon.camunda.example.hotel.CreateHotel
import io.holunda.axon.camunda.example.hotel.HotelBooked
import mu.KLogging
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
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
    const val TICKET_NUMBER = "flightTicketNumber"
  }

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
    const val BOOK_FLIGHT = "bookFlight"
  }

  object Signals {
    const val HOTEL_BOOKED = "hotelBooked"
    const val FLIGHT_BOOKED = "flightBooked"
  }
}

@Configuration
class TravelReservationConfiguration(private val gateway: CommandGateway) {

  @Bean
  fun init() = object : DefaultSmartLifecycle(1000) {
    override fun onStart() {

      val now = LocalDateTime.now();

      gateway.send<Any, Any>(CreateHotel("astoria", "Hamburg"), LoggingCallback.INSTANCE)
      gateway.send<Any, Any>(CreateFlight("LH-123", now, now.plusHours(2), "HAM", "MUC", 10), LoggingCallback.INSTANCE)
      gateway.send<Any, Any>(CreateFlight("LH-124", now.plusHours(8), now.plusHours(10), "MUC", "HAM", 10), LoggingCallback.INSTANCE)
    }
  }

  @Bean
  fun objectFactory() = Jackson2ObjectMapperBuilder().modules(JavaTimeModule()).build<ObjectMapper>()

  companion object : KLogging()

  @Autowired
  fun configure(registry: CamundaAxonEventCommandFactoryRegistry) {

    registry.register(object : AbstractEventCommandFactory(TravelProcess.KEY) {


      /**
       * Commands
       */
      override fun command(messageName: String, execution: DelegateExecution): Any {
        val reservation = execution.getVariable(TravelProcess.Variables.RESERVATION) as Reservation

        // create a mapper between payload variables and command objects based on the message name
        return when (messageName) {
          TravelProcess.Messages.BOOK_HOTEL -> BookHotel(arrival = reservation.from, departure = reservation.to, guestName = reservation.name, hotelName = reservation.hotel)
          TravelProcess.Messages.BOOK_FLIGHT -> BookFlight(flightNumber = reservation.flightNumber, guestName = reservation.name)
          else -> super.command(messageName, execution)
        }
      }

      /**
       * Events
       */
      override fun event(payload: Any, metadata: MetaData): CamundaEvent? =
        when (payload) {
          is HotelBooked -> {
            // return hotel reservation id into the process payload
            CamundaEvent(TravelProcess.Signals.HOTEL_BOOKED, mapOf<String, Any>(TravelProcess.Variables.HOTEL_RESERVATION_ID to payload.reservationId))
          }
          is FlightBooked -> {
            CamundaEvent(TravelProcess.Signals.FLIGHT_BOOKED, mapOf<String, Any>(TravelProcess.Variables.TICKET_NUMBER to payload.ticketNumber))
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
      Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "astoria", "LH-124")
    )
  }
}

data class Reservation(
  val name: String,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val from: LocalDate,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val to: LocalDate,
  val hotel: String,
  val flightNumber: String
) : Serializable {
  constructor() : this("", LocalDate.MIN, LocalDate.MIN, "", "")
}


fun interval(start: LocalDate, end: LocalDate) = Interval.of(start.atStartOfDay().toInstant(ZoneOffset.UTC), end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))

