package io.holunda.axon.camunda.example.travel.compensation.process

import io.holunda.axon.camunda.AbstractEventCommandFactory
import io.holunda.axon.camunda.CamundaAxonEventCommandFactoryRegistry
import io.holunda.axon.camunda.CamundaEvent
import io.holunda.axon.camunda.example.travel.compensation.travel.airline.*
import io.holunda.axon.camunda.example.travel.compensation.travel.hotel.*
import io.holunda.spring.DefaultSmartLifecycle
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.MetaData
import org.camunda.bpm.engine.delegate.BpmnError
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
      gateway.send<Any, Any>(CreateFlight("LH-123", now, now.plusHours(2), "HAM", "MUC", 0), LoggingCallback.INSTANCE)
      gateway.send<Any, Any>(CreateFlight("LH-124", now.plusHours(8), now.plusHours(10), "MUC", "HAM", 10), LoggingCallback.INSTANCE)
    }
  }


  @Autowired
  fun configure(registry: CamundaAxonEventCommandFactoryRegistry) {

    registry.register(object : AbstractEventCommandFactory(MessageBasedTravelProcessWithCompensation.KEY) {

      /**
       * Commands
       */
      override fun command(messageName: String, execution: DelegateExecution): Any {
        val reservation = execution.getVariable(MessageBasedTravelProcessWithCompensation.Variables.RESERVATION) as Reservation

        // create a mapper between payload variables and command objects based on the message name
        return when (messageName) {
          MessageBasedTravelProcessWithCompensation.Messages.BOOK_HOTEL ->
            BookHotel(
              arrival = reservation.from,
              departure = reservation.to,
              guestName = reservation.name,
              hotelName = reservation.hotel,
              reservationId = reservation.id)
          MessageBasedTravelProcessWithCompensation.Messages.CANCEL_HOTEL ->
            CancelHotel(
              hotelName = reservation.hotel,
              hotelConfirmationCode = execution.getVariable(MessageBasedTravelProcessWithCompensation.Variables.HOTEL_CONFIRMATION_CODE) as String,
              reservationId = reservation.id)
          MessageBasedTravelProcessWithCompensation.Messages.BOOK_FLIGHT ->
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
              name = MessageBasedTravelProcessWithCompensation.Messages.HOTEL_BOOKED,
              variables = mapOf<String, Any>(MessageBasedTravelProcessWithCompensation.Variables.HOTEL_CONFIRMATION_CODE to payload.hotelConfirmationCode),
              correlationVariableName = MessageBasedTravelProcessWithCompensation.Variables.RESERVATION_ID)

          is HotelCancelled ->
            CamundaEvent(
              name = MessageBasedTravelProcessWithCompensation.Messages.HOTEL_CANCELLED,
              variables = mapOf(),
              correlationVariableName = MessageBasedTravelProcessWithCompensation.Variables.RESERVATION_ID)


          is FlightBooked ->
            CamundaEvent(
              name = MessageBasedTravelProcessWithCompensation.Messages.FLIGHT_BOOKED,
              variables = mapOf<String, Any>(MessageBasedTravelProcessWithCompensation.Variables.TICKET_NUMBER to payload.ticketNumber),
              correlationVariableName = MessageBasedTravelProcessWithCompensation.Variables.RESERVATION_ID)
          else -> null
        }

      override fun error(cause: Throwable): BpmnError? =
        when (cause) {
          is NoSeatsAvailable ->
            BpmnError(MessageBasedTravelProcessWithCompensation.Errors.ERROR_BOOKING_FLIGHT, cause.message)
          is FlightBookingNotPossibleException ->
            BpmnError(MessageBasedTravelProcessWithCompensation.Errors.ERROR_BOOKING_FLIGHT, cause.message)
          is HotelReservationNotPossibleException ->
            BpmnError(MessageBasedTravelProcessWithCompensation.Errors.ERROR_BOOKING_HOTEL, cause.message)
          else -> null
        }


    })
  }
}
