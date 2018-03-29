package io.holunda.axon.camunda.example.flight

import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDateTime

typealias Airport = String
typealias FlightNumber = String

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
  val guestName: String
)

data class FlightCreated(
  val flightNumber: FlightNumber,
  val arrival: LocalDateTime,
  val departure: LocalDateTime,
  val from: Airport,
  val to: Airport,
  val seats: Int
)

data class FlightBooked(
  val flightNumber: FlightNumber,
  val guestName: String,
  val arrival: LocalDateTime,
  val departure: LocalDateTime,
  val ticketNumber: String
)

data class FlightBookingNotPossibleException(override val message: String) : Exception(message)

@Aggregate
class Flight() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var flightNumber: FlightNumber
  private lateinit var arrivalTime: LocalDateTime
  private lateinit var departureTime: LocalDateTime
  private lateinit var arrivalAirport: Airport
  private lateinit var departureAirport: Airport
  private var availableSeats: Int = 0

  @CommandHandler
  constructor(c: CreateFlight) : this() {
    apply(FlightCreated(c.flightNumber, c.arrival, c.departure, c.from, c.to, c.seats))
  }

  @CommandHandler
  fun handle(c: BookFlight) {
    if (this.availableSeats > 0) {
      apply(FlightBooked(
        flightNumber = this.flightNumber,
        guestName = c.guestName,
        departure = this.departureTime,
        arrival = this.arrivalTime,
        ticketNumber = this.flightNumber + ":" + availableSeats)
      )
    } else {
      throw NoSeatsAvailable(this.flightNumber)
    }
  }

  @EventSourcingHandler
  fun on(e: FlightCreated) {
    this.flightNumber = e.flightNumber
    this.arrivalTime = e.arrival
    this.departureTime = e.departure
    this.arrivalAirport = e.from
    this.departureAirport = e.to
    this.availableSeats = e.seats
  }

  @EventSourcingHandler
  fun on(e: FlightBooked) {
    // decrease number of seats
    this.availableSeats.dec()
  }
}

class NoSeatsAvailable(val flightNumber: FlightNumber) : Exception("No seats in $flightNumber available.") {

}
