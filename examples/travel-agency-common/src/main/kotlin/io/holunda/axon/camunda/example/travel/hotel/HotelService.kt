package io.holunda.axon.camunda.example.travel.hotel

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@ProcessingGroup("Hotel")
@Component
class HotelInformationService {

  companion object : KLogging()

  private val hotels : MutableMap<String, MutableSet<String>> = mutableMapOf()

  fun listHotels(city: String) = hotels[city]?.toSet()?:setOf<String>()


  @EventHandler
  fun on(e: HotelCreated) {
    val cityHotels = hotels[e.city]?: mutableSetOf()
    cityHotels.add(e.hotelName)
    hotels[e.city] = cityHotels
    logger.info { "Hotel added ${e.hotelName} in ${e.city}" }
  }

}
