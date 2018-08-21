package io.holunda.axon.camunda.example.airline

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service for searching for flights.
 */
@Service
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
    logger.info { "Flight added ${e.flightNumber} from ${e.arrival} to ${e.departure}" }
  }

  data class FlightInfo(
    val flightNumber: FlightNumber,
    val departureAirport: Airport,
    val arrivalAirport: Airport,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime
  )
}
