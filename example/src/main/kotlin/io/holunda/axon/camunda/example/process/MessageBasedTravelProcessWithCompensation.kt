package io.holunda.axon.camunda.example.process

object MessageBasedTravelProcessWithCompensation {

  const val KEY = "travel-compensation"
  const val BPMN = "message-based-travel-compensation.bpmn"

  object Variables {
    const val RESERVATION = MessageBasedTravelProcess.Variables.RESERVATION
    const val RESERVATION_ID = MessageBasedTravelProcess.Variables.RESERVATION_ID
    const val HOTEL_CONFIRMATION_CODE = MessageBasedTravelProcess.Variables.HOTEL_CONFIRMATION_CODE
    const val TICKET_NUMBER = MessageBasedTravelProcess.Variables.TICKET_NUMBER
  }

  object Messages {
    const val BOOK_HOTEL = MessageBasedTravelProcess.Messages.BOOK_HOTEL
    const val CANCEL_HOTEL = "cancelHotel"
    const val BOOK_FLIGHT = MessageBasedTravelProcess.Messages.BOOK_FLIGHT

    const val HOTEL_BOOKED = MessageBasedTravelProcess.Messages.HOTEL_BOOKED
    const val FLIGHT_BOOKED = MessageBasedTravelProcess.Messages.FLIGHT_BOOKED
    const val HOTEL_CANCELLED = "hotelCancelled"
  }

  object Errors {
    const val ERROR_BOOKING_FLIGHT = "ErrorBookingFlight"
    const val ERROR_BOOKING_HOTEL = "ErrorBookingHotel"
  }

}
