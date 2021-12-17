package io.holunda.axon.camunda.example.travel.compensation

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.example.travel.minimal.TravelAgencyApplicationMinimal
import io.holunda.axon.camunda.example.travel.process.payload.Reservation
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TravelAgencyApplicationMinimal::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
class SerializationITest {

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Test
  fun testSerialization() {

    val reservation = Reservation(
      name = "Kermit",
      from = LocalDate.now(),
      to = LocalDate.now().plusDays(2),
      flightNumber = "LH-123",
      hotel = "Astoria",
      id = UUID.randomUUID().toString()
    )
    val serialized = objectMapper.writeValueAsString(reservation)
    val again = objectMapper.readValue(serialized, Reservation::class.java)

    Assertions.assertThat(again).isEqualTo(reservation)
  }
}
