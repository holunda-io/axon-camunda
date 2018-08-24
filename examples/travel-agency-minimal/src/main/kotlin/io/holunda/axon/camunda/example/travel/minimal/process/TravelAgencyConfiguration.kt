package io.holunda.axon.camunda.example.travel.minimal.process

import io.holunda.axon.camunda.AbstractEventCommandFactory
import io.holunda.axon.camunda.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.CamundaEvent
import io.holunda.axon.camunda.example.travel.airline.BookFlight
import io.holunda.axon.camunda.example.travel.airline.CreateFlight
import io.holunda.axon.camunda.example.travel.airline.FlightBooked
import io.holunda.axon.camunda.example.travel.hotel.BookHotel
import io.holunda.axon.camunda.example.travel.hotel.CreateHotel
import io.holunda.axon.camunda.example.travel.hotel.HotelBooked
import io.holunda.axon.camunda.spring.DefaultSmartLifecycle
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime


@Configuration
open class TravelAgencyConfiguration(private val gateway: CommandGateway) {

  @Bean
  fun init() = object : DefaultSmartLifecycle(1000) {
    override fun onStart() {

      val now = LocalDateTime.now();

      gateway.send<Any, Any>(CreateHotel("Astoria", "Hamburg"), LoggingCallback.INSTANCE)
      gateway.send<Any, Any>(CreateFlight("LH-123", now, now.plusHours(2), "HAM", "MUC", 10), LoggingCallback.INSTANCE)
      gateway.send<Any, Any>(CreateFlight("LH-124", now.plusHours(8), now.plusHours(10), "MUC", "HAM", 10), LoggingCallback.INSTANCE)
    }
  }


  @Autowired
  fun configure(registry: CamundaAxonEventCommandFactoryRegistry) {

    registry.register(object : AbstractEventCommandFactory(MessageBasedTravelProcess.KEY) {

      /**
       * Commands
       */
      override fun command(messageName: String, execution: DelegateExecution): Any {
        val reservation = execution.getVariable(MessageBasedTravelProcess.Variables.RESERVATION) as Reservation

        // create a mapper between payload variables and command objects based on the message name
        return when (messageName) {
          MessageBasedTravelProcess.Messages.BOOK_HOTEL ->
            BookHotel(
              arrival = reservation.from,
              departure = reservation.to,
              guestName = reservation.name,
              hotelName = reservation.hotel,
              reservationId = reservation.id)
          MessageBasedTravelProcess.Messages.BOOK_FLIGHT ->
            BookFlight(
              flightNumber = reservation.flightNumber,
              guestName = reservation.name,
              reservationId = reservation.id)
          else -> super.command(messageName, execution)
        }
      }

      /**
       * Events
       */
      override fun event(payload: Any, metadata: MetaData): CamundaEvent? =
        when (payload) {
          is HotelBooked ->
            // return hotel reservation id into the process payload
            CamundaEvent(
              name = MessageBasedTravelProcess.Messages.HOTEL_BOOKED,
              variables = mapOf<String, Any>(MessageBasedTravelProcess.Variables.HOTEL_CONFIRMATION_CODE to payload.hotelConfirmationCode),
              correlationVariableName = MessageBasedTravelProcess.Variables.RESERVATION_ID)

          is FlightBooked ->
            CamundaEvent(
              name = MessageBasedTravelProcess.Messages.FLIGHT_BOOKED,
              variables = mapOf<String, Any>(MessageBasedTravelProcess.Variables.TICKET_NUMBER to payload.ticketNumber),
              correlationVariableName = MessageBasedTravelProcess.Variables.RESERVATION_ID)
          else -> null
        }

    })
  }
}

