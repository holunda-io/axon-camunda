package io.holunda.axon.camunda.example

import io.holunda.axon.camunda.example.flight.Airport
import io.holunda.axon.camunda.example.flight.FlightCreated
import io.holunda.axon.camunda.example.flight.FlightNumber
import io.holunda.axon.camunda.example.hotel.HotelCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LoggingService : JavaDelegate {

  companion object : KLogging()

  override fun execute(execution: DelegateExecution) {
    logger.info { "Executed in ${execution.activityInstanceId}" }
    execution.variables.forEach { name, value -> logger.info { "\t$name : '$value'" } }
  }
}


/**
 * Service for searching for flights.
 */
@Component
@ProcessingGroup("Airline")
class AirlineInformationService {

  companion object : KLogging()

  val flights: MutableMap<Pair<Airport, Airport>, MutableSet<FlightInfo>> = mutableMapOf()

  fun searchFlights(departureAirport: Airport, arrivalAirport: Airport): Set<FlightInfo> {
    return flights[Pair(departureAirport, arrivalAirport)]?.toSet() ?: setOf<FlightInfo>()
  }

  @EventHandler
  fun on(e: FlightCreated) {

    val destinationFlights = flights[Pair(e.from, e.to)] ?: mutableSetOf()
    destinationFlights.add(FlightInfo(
      flightNumber = e.flightNumber,
      departureAirport = e.from,
      arrivalAirport = e.to,
      departureDate = e.departure,
      arrivalDate = e.arrival
    ))
    flights[Pair(e.from, e.to)] = destinationFlights
    logger.info { "Flight added ${e.flightNumber}" }
  }

  data class FlightInfo(
    val flightNumber: FlightNumber,
    val departureAirport: Airport,
    val arrivalAirport: Airport,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime
  )
}

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
