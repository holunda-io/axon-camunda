package io.holunda.axon.camunda.example.travel.airline

import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDateTime

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
        ticketNumber = this.flightNumber + ":" + availableSeats,
        reservationId = c.reservationId)
      )
    } else {
      throw NoSeatsAvailable(this.flightNumber)
    }
  }

  @CommandHandler
  fun handle(c: CancelFlight) {
      apply(FlightCancelled(
        flightNumber = this.flightNumber,
        guestName = c.guestName,
        departure = this.departureTime,
        arrival = this.arrivalTime,
        reservationId = c.reservationId)
      )
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

  @EventSourcingHandler
  fun on(e: FlightCancelled) {
    // increase number of seats
    this.availableSeats.inc()
  }

}
