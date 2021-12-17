package io.holunda.axon.camunda.example.travel.minimal.process

/**
 * Process constants
 */
object MessageBasedTravelProcess {
  const val KEY = "travel"
  const val BPMN = "message-based-travel.bpmn"
  
  object Messages {
    const val BOOK_HOTEL = "bookHotel"
    const val BOOK_FLIGHT = "bookFlight"
    const val HOTEL_BOOKED = "hotelBooked"
    const val FLIGHT_BOOKED = "flightBooked"
  }
}


