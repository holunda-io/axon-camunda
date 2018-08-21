package io.holunda.axon.camunda.example.airline

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.example.Reservation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    assertThat(again).isEqualTo(reservation)
  }
}
