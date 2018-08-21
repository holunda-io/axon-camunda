package io.holunda.axon.camunda.example

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.util.*

/**
 * Process constants
 */
object TravelProcess {
  const val KEY = "travel"
  const val BPMN = "travel.bpmn"

  object Variables {
    const val RESERVATION = "reservation"
    const val RESERVATION_ID = "reservationId"
    const val HOTEL_CONFIRMATION_CODE = "hotelConfirmationCode"
    const val TICKET_NUMBER = "flightTicketNumber"
  }

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
    const val BOOK_FLIGHT = "bookFlight"
    const val HOTEL_BOOKED = "hotelBooked"
    const val FLIGHT_BOOKED = "flightBooked"
  }
}

data class Reservation(
  val name: String,
  @get: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val from: LocalDate,
  @get: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val to: LocalDate,
  val hotel: String,
  val flightNumber: String,
  val id: String = UUID.randomUUID().toString()
)


