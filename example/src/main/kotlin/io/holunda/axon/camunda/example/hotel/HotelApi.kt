package io.holunda.axon.camunda.example.hotel

import io.holunda.axon.camunda.EventCorrelationId
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDate
import java.util.*

data class CreateHotel(
  @TargetAggregateIdentifier
  val hotelName: String,
  val city: String
)

data class BookHotel(
  val arrival: LocalDate,
  val departure: LocalDate,
  val guestName: String,
  @TargetAggregateIdentifier
  val hotelName: String,
  val reservationId: String = UUID.randomUUID().toString()
)

data class HotelCreated(
  val hotelName: String,
  val city: String
)


data class HotelBooked(
  val arrival: LocalDate,
  val departure: LocalDate,
  val guestName: String,
  val hotelName: String,
  @EventCorrelationId
  val reservationId: String,
  val hotelConfirmationCode: String
)

data class HotelReservationNotPossibleException(override val message: String) : Exception(message)
