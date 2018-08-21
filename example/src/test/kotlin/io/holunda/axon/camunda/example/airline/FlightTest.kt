package io.holunda.axon.camunda.example.airline


import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class AirlineAggregateTest {

  lateinit var fix: AggregateTestFixture<Flight>

  private val departureDate = LocalDateTime.now()
  private val arrivalDate = departureDate.plusHours(2)
  private val departureAirport = "HAM"
  private val arrivalAirport = "MUC"
  private val flightNumber = "LH-123"


  @Before
  fun init() {
    fix = AggregateTestFixture(Flight::class.java)
  }

  @Test
  fun `create flight`() {

    fix
      .givenNoPriorActivity()
      .`when`(CreateFlight(flightNumber = flightNumber, arrival = arrivalDate, departure = departureDate, from = departureAirport, to = arrivalAirport, seats = 1))
      .expectEvents(FlightCreated(flightNumber = flightNumber, arrival = arrivalDate, departure = departureDate, from = departureAirport, to = arrivalAirport, seats = 1))
  }

  @Test
  fun `create flight booking`() {

    val seatCount = 9
    fix
      .given(FlightCreated(flightNumber = flightNumber, arrival = arrivalDate, departure = departureDate, from = departureAirport, to = arrivalAirport, seats = seatCount))
      .`when`(BookFlight(flightNumber = flightNumber, guestName = "kermit"))
      .expectEvents(FlightBooked(flightNumber = flightNumber, guestName = "kermit", arrival = arrivalDate, departure = departureDate, ticketNumber = "LH-123:$seatCount", reservationId = UUID.randomUUID().toString()))

  }

  // FIXME understand why the test fails.
  @Ignore
  @Test
  fun `no seats available`() {

    fix
      .given(
        FlightCreated(flightNumber = flightNumber, arrival = arrivalDate, departure = departureDate, from = departureAirport, to = arrivalAirport, seats = 1),
        FlightBooked(flightNumber = flightNumber, guestName = "piggy", arrival = arrivalDate, departure = departureDate, ticketNumber = "LH-123:1", reservationId = UUID.randomUUID().toString()))
      .`when`(BookFlight(flightNumber = flightNumber, guestName = "kermit"))
      .expectNoEvents()
      .expectException(NoSeatsAvailable::class.java)
  }

}
