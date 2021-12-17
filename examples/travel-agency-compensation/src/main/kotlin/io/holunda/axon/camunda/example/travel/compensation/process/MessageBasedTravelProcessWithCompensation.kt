package io.holunda.axon.camunda.example.travel.compensation.process

object MessageBasedTravelProcessWithCompensation {

  const val KEY = "travel-compensation"
  const val BPMN = "message-based-travel-compensation.bpmn"

  object Messages {
    const val BOOK_HOTEL = "bookHotel"
    const val BOOK_FLIGHT = "bookFlight"
    const val CANCEL_HOTEL = "cancelHotel"
    const val HOTEL_BOOKED = "hotelBooked"
    const val HOTEL_CANCELLED = "hotelCancelled"
    const val FLIGHT_BOOKED = "flightBooked"
  }

  object Errors {
    const val ERROR_BOOKING_FLIGHT = "ErrorBookingFlight"
    const val ERROR_BOOKING_HOTEL = "ErrorBookingHotel"
  }

}
