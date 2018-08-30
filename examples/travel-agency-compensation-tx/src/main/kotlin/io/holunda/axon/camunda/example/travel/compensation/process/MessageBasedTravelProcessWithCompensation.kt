package io.holunda.axon.camunda.example.travel.compensation.process

object MessageBasedTravelProcessWithCompensation {

  const val KEY = "travel-compensation-tx"
  const val BPMN = "message-based-travel-compensation-tx.bpmn"

  object Variables {
    const val RESERVATION = "reservation"
    const val RESERVATION_ID = "reservationId"
    const val HOTEL_CONFIRMATION_CODE = "hotelConfirmationCode"
    const val TICKET_NUMBER = "flightTicketNumber"
    const val PAYED = "payed"
  }

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
    const val CANCEL_HOTEL = "cancelHotel"
    const val HOTEL_BOOKED = "hotelBooked"
    const val HOTEL_CANCELLED = "hotelCancelled"

    const val BOOK_FLIGHT = "bookFlight"
    const val CANCEL_FLIGHT = "cancelFlight"
    const val FLIGHT_BOOKED = "flightBooked"
    const val FLIGHT_CANCELLED = "flightCancelled"
  }

  object Errors {
    const val ERROR_BOOKING_FLIGHT = "ErrorBookingFlight"
    const val ERROR_BOOKING_HOTEL = "ErrorBookingHotel"
    const val ERROR_EXECUTING_PAYMENT = "ErrorExecutingPayment"
  }

}
