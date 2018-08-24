package io.holunda.axon.camunda.example.travel.hotel

import io.holunda.axon.camunda.example.travel.end
import io.holunda.axon.camunda.example.travel.interval
import io.holunda.axon.camunda.example.travel.start
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.threeten.extra.Interval


@Aggregate
class HotelBooking() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var hotelName: String
  private val reserved = mutableMapOf<String, Interval>()

  @CommandHandler
  constructor(c: CreateHotel) : this() {
    apply(HotelCreated(c.hotelName, c.city))
  }

  @CommandHandler
  fun handle(c: BookHotel) {
    if (reserved.values.filter { it.overlaps(interval(c.arrival, c.departure)) }.count() != 0) {
      throw HotelReservationNotPossibleException("No rooms free")
    }
    apply(HotelBooked(
      arrival = c.arrival,
      departure = c.departure,
      guestName = c.guestName,
      hotelName = c.hotelName,
      hotelConfirmationCode = "${c.hotelName}_${c.guestName}_${c.reservationId}",
      reservationId = c.reservationId
    ))
  }

  @CommandHandler
  fun handle(c: CancelHotel) {
    if (reserved.containsKey(c.hotelConfirmationCode)) {
      apply(
        HotelCancelled(
          arrival = start(reserved[c.hotelConfirmationCode]!!),
          departure = end(reserved[c.hotelConfirmationCode]!!),
          hotelConfirmationCode = c.hotelConfirmationCode,
          reservationId = c.reservationId
        )
      )
    }
  }

  @EventSourcingHandler
  fun on(e: HotelCreated) {
    this.hotelName = e.hotelName
  }

  @EventSourcingHandler
  fun on(e: HotelBooked) {
    reserved.put(e.hotelConfirmationCode, interval(e.arrival, e.departure))
  }
}
