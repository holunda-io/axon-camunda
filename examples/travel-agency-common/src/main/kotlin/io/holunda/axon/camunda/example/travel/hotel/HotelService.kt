package io.holunda.axon.camunda.example.travel.hotel

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@ProcessingGroup("Hotel")
@Component
class HotelInformationService {

  companion object : KLogging()

  private val hotels: MutableMap<String, MutableSet<String>> = mutableMapOf()

  fun listHotels(city: String) = hotels[city]?.toSet() ?: setOf<String>()


  @EventHandler
  fun on(e: HotelCreated) {
    val cityHotels = hotels[e.city] ?: mutableSetOf()
    cityHotels.add(e.hotelName)
    hotels[e.city] = cityHotels
    logger.info { "Hotel added ${e.hotelName} in ${e.city}" }
  }

  @EventHandler
  fun on(e: HotelBooked) {
    logger.info { "Hotel booked ${e.hotelName} for ${e.guestName} from ${e.arrival} to ${e.departure} confirmed by ${e.hotelConfirmationCode}" }
  }

  @EventHandler
  fun on(e: HotelCancelled) {
    logger.info { "Hotel booking cancelled for confirmation code: ${e.hotelConfirmationCode} from ${e.arrival} to ${e.departure}." }
  }

}
