package io.holunda.axon.camunda.example.flight


import io.holunda.axon.camunda.example.flight.Flight
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime

class AirlineAggregateTest {

  lateinit var fix: AggregateTestFixture<Flight>
  val from = LocalDateTime.now()
  val to = from.plusHours(2)


  @Before
  fun init() {
    fix = AggregateTestFixture(io.holunda.axon.camunda.example.flight.Flight::class.java)
  }

  @Test
  fun `create flight`() {

    fix
      .givenNoPriorActivity()
      .`when`(CreateFlight(flightNumber = "LH-123", arrival = from, departure = to, from = "HAM", to = "MUC", seats = 1))
      .expectEvents(FlightCreated(flightNumber = "LH-123", arrival = from, departure = to, from = "HAM", to = "MUC", seats = 1))
  }

  @Test
  fun `create flight booking`() {

    val seatCount = 9
    fix
      .given(FlightCreated(flightNumber = "LH-123", arrival = from, departure = to, from = "HAM", to = "MUC", seats = seatCount))
      .`when`(BookFlight(flightNumber = "LH-123", guestName = "kermit"))
      .expectEvents(FlightBooked(flightNumber = "LH-123", guestName = "kermit", arrival = from, departure = to, ticketNumber = "LH-123:" + seatCount ))

  }

  // FIXME understand why the test fails.
  @Ignore
  @Test
  fun `no seats available`() {

    fix
      .given(
        FlightCreated(flightNumber = "LH-123", arrival = from, departure = to, from = "HAM", to = "MUC", seats = 1),
        FlightBooked(flightNumber = "LH-123", guestName = "piggy", arrival = from, departure = to, ticketNumber = "LH-123:1"))
      .`when`(BookFlight(flightNumber = "LH-123", guestName = "kermit"))
      .expectNoEvents()
      .expectException(NoSeatsAvailable::class.java)
  }

}
