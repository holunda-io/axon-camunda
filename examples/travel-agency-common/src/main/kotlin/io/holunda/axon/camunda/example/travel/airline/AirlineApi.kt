package io.holunda.axon.camunda.example.travel.airline

import io.holunda.axon.camunda.EventCorrelationId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*

typealias Airport = String
typealias FlightNumber = String

/**
 * Commands
 */

data class CreateFlight(
  @TargetAggregateIdentifier
  val flightNumber: FlightNumber,
  val arrival: LocalDateTime,
  val departure: LocalDateTime,
  val from: Airport,
  val to: Airport,
  val seats: Int
)

data class BookFlight(
  @TargetAggregateIdentifier
  val flightNumber: FlightNumber,
  val guestName: String,
  val reservationId: String = UUID.randomUUID().toString()
)

data class CancelFlight(
  @TargetAggregateIdentifier
  val flightNumber: FlightNumber,
  val guestName: String,
  val reservationId: String = UUID.randomUUID().toString()
)


/**
 * Events
 */

data class FlightCreated(
  val flightNumber: FlightNumber,
  val arrival: LocalDateTime,
  val departure: LocalDateTime,
  val from: Airport,
  val to: Airport,
  val seats: Int
)

data class FlightBooked(
  @EventCorrelationId
  val reservationId: String,
  val flightNumber: FlightNumber,
  val guestName: String,
  val arrival: LocalDateTime,
  val departure: LocalDateTime,
  val ticketNumber: String
)

data class FlightCancelled(
  @EventCorrelationId
  val reservationId: String,
  val flightNumber: FlightNumber,
  val guestName: String,
  val arrival: LocalDateTime,
  val departure: LocalDateTime
)


data class FlightBookingNotPossibleException(override val message: String) : Exception(message)
data class NoSeatsAvailable(val flightNumber: FlightNumber) : Exception("No seats in $flightNumber available.")
