package io.holunda.axon.camunda.example.travel.hotel

import io.holunda.axon.camunda.EventCorrelationId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate
import java.util.*

data class CreateHotel(
  @TargetAggregateIdentifier
  val hotelName: String,
  val city: String
)

data class BookHotel(
  val arrival: LocalDate,
  @TargetAggregateIdentifier
  val hotelName: String,
  val departure: LocalDate,
  val guestName: String,
  val reservationId: String = UUID.randomUUID().toString()
)

data class CancelHotel(
  @TargetAggregateIdentifier
  val hotelName: String,
  val reservationId: String = UUID.randomUUID().toString()
)


data class HotelCreated(
  val hotelName: String,
  val city: String
)

data class HotelBooked(
  @EventCorrelationId
  val reservationId: String,
  val arrival: LocalDate,
  val departure: LocalDate,
  val guestName: String,
  val hotelName: String,
  val hotelConfirmationCode: String
)

data class HotelCancelled(
  @EventCorrelationId
  val reservationId: String,
  val arrival: LocalDate,
  val departure: LocalDate,
  val hotelConfirmationCode: String
)

data class HotelReservationNotPossibleException(override val message: String) : Exception(message)
