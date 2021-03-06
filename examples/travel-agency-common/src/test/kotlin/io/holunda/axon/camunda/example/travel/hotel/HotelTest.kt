package io.holunda.axon.camunda.example.travel.hotel

import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test
import java.time.LocalDate

class HotelAggregateTest {

  private val fix: AggregateTestFixture<HotelBooking> = AggregateTestFixture(HotelBooking::class.java)

  @Test
  fun `create one booking`() {

    val arrival = LocalDate.now()
    val departure = arrival.plusDays(3)

    fix
      .given(HotelCreated("astoria", "Hamburg"))
      .`when`(BookHotel(
        arrival = arrival,
        departure = departure,
        guestName = "kermit",
        hotelName = "astoria",
        reservationId = "4711"
      ))
      .expectEvents(
        HotelBooked(
          arrival = arrival,
          departure = departure,
          guestName = "kermit",
          hotelName = "astoria",
          reservationId = "4711",
          hotelConfirmationCode = "astoria_kermit_4711"
        )
      )
  }

  @Test
  fun `fail to create overlapping booking`() {

    val arrival = LocalDate.now()
    val departure = arrival.plusDays(3)

    fix
      .given(
        HotelCreated("astoria", "Hamburg"),
        HotelBooked(
          arrival = arrival,
          departure = departure,
          guestName = "kermit",
          hotelName = "astoria",
          reservationId = "4711",
          hotelConfirmationCode = "astoria_kermit_4711"
        ))
      .`when`(
        BookHotel(
          arrival = arrival,
          departure = departure,
          guestName = "piggy",
          hotelName = "astoria",
          reservationId = "4712"
        )
      )
      .expectException(HotelReservationNotPossibleException::class.java)
  }

}
