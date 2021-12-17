package io.holunda.axon.camunda.example.travel.compensation.rest

import io.holunda.axon.camunda.example.travel.compensation.process.MessageBasedTravelProcessWithCompensation
import io.holunda.axon.camunda.example.travel.process.CommonVariables
import io.holunda.axon.camunda.example.travel.process.payload.Reservation
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.variable.Variables
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class ProcessStarter(private val runtime: RuntimeService) {

  @PostMapping("/travel/lh123")
  fun startPiggyLH123(): ResponseEntity<String> {

    val reservation = Reservation("piggy", LocalDate.now(), LocalDate.now().plusDays(2), "Astoria", "LH-123")

    val instanceId = runtime
      .startProcessInstanceByKey(
        MessageBasedTravelProcessWithCompensation.KEY,
        Variables.createVariables().putValue(CommonVariables.RESERVATION, reservation)
      )
      .processInstanceId

    return ResponseEntity.ok(instanceId)
  }


  @PostMapping("/travel/lh124")
  fun startKermit124(): ResponseEntity<String> {

    val reservation = Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "Four Seasons", "LH-124")

    val instanceId = runtime
      .startProcessInstanceByKey(
        MessageBasedTravelProcessWithCompensation.KEY,
        Variables.createVariables().putValue(CommonVariables.RESERVATION, reservation)
      )
      .processInstanceId

    return ResponseEntity.ok(instanceId)
  }

}
