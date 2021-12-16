package io.holunda.axon.camunda.example.travel.compensation

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.axon.camunda.example.travel.minimal.TravelAgencyApplication
import io.holunda.axon.camunda.example.travel.minimal.process.MessageBasedTravelProcess
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.assertj.core.api.Assertions
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat
import org.camunda.bpm.engine.variable.Variables
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TravelAgencyApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
class ProcessITest {

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Autowired
  lateinit var runtimeService: RuntimeService

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

  @Test
  fun testStartingOfProcess() {
    // GIVEN a reservation
    val reservation = Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "Astoria", "LH-124")

    // WHEN we start a process
    val processInstance = runtimeService
      .startProcessInstanceByKey(
        MessageBasedTravelProcess.KEY,
        Variables.createVariables().putValue(MessageBasedTravelProcess.Variables.RESERVATION, reservation)
      )

    // Then
    assertThat(processInstance).isStarted()
  }
}
