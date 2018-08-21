package io.holunda.axon.camunda.example

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.holunda.axon.camunda.AbstractEventCommandFactory
import io.holunda.axon.camunda.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.CamundaEvent
import io.holunda.axon.camunda.example.airline.BookFlight
import io.holunda.axon.camunda.example.airline.CreateFlight
import io.holunda.axon.camunda.example.airline.FlightBooked
import io.holunda.axon.camunda.example.hotel.BookHotel
import io.holunda.axon.camunda.example.hotel.CreateHotel
import io.holunda.axon.camunda.example.hotel.HotelBooked
import io.holunda.spring.DefaultSmartLifecycle
import mu.KLogging
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDateTime


@Configuration
class TravelReservationConfiguration(private val gateway: CommandGateway) {

  companion object : KLogging()

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
  fun objectFactory(): ObjectMapper {
    return Jackson2ObjectMapperBuilder()
      .modules(JavaTimeModule(), KotlinModule())
      .build()
  }


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
          TravelProcess.Messages.BOOK_HOTEL -> BookHotel(arrival = reservation.from, departure = reservation.to, guestName = reservation.name, hotelName = reservation.hotel, reservationId = reservation.id)
          TravelProcess.Messages.BOOK_FLIGHT -> BookFlight(flightNumber = reservation.flightNumber, guestName = reservation.name, reservationId = reservation.id)
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
            CamundaEvent(TravelProcess.Messages.HOTEL_BOOKED, mapOf<String, Any>(TravelProcess.Variables.HOTEL_CONFIRMATION_CODE to payload.hotelConfirmationCode), TravelProcess.Variables.RESERVATION_ID)
          }
          is FlightBooked -> {
            CamundaEvent(TravelProcess.Messages.FLIGHT_BOOKED, mapOf<String, Any>(TravelProcess.Variables.TICKET_NUMBER to payload.ticketNumber), TravelProcess.Variables.RESERVATION_ID)
          }
          else -> null
        }

    })
  }
}

