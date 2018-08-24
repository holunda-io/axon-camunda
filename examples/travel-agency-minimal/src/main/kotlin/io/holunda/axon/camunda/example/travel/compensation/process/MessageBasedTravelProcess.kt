package io.holunda.axon.camunda.example.travel.compensation.process

/**
 * Process constants
 */
object MessageBasedTravelProcess {
  const val KEY = "travel"
  const val BPMN = "message-based-travel.bpmn"

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


