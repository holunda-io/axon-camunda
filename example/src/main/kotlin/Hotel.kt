package io.holunda.axon.camunda.example.hotel

import io.holunda.axon.camunda.example.interval
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.threeten.extra.Interval
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
  val reservationId: String
)

data class HotelReservationNotPossibleException(override val message: String) : Exception(message)

@Aggregate
class HotelBooking() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var hotelName: String
  private val reserved = mutableSetOf<Interval>()

  @CommandHandler
  constructor(c: CreateHotel) : this() {
    apply(HotelCreated(c.hotelName, c.city))
  }

  @CommandHandler
  fun handle(c: BookHotel) {
    if (reserved.filter { it.overlaps(interval(c.arrival, c.departure)) }.count() != 0) {
      throw HotelReservationNotPossibleException("No rooms free")
    }
    apply(HotelBooked(
      arrival = c.arrival,
      departure = c.departure,
      guestName = c.guestName,
      hotelName = c.hotelName,
      reservationId = c.reservationId
    ))
  }

  @EventSourcingHandler
  fun on(e: HotelCreated) {
    this.hotelName = e.hotelName
  }

  @EventSourcingHandler
  fun on(e: HotelBooked) {
    reserved.add(interval(e.arrival, e.departure))
  }
}
