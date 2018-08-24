package io.holunda.axon.camunda.example.rest

import io.holunda.axon.camunda.example.process.MessageBasedTravelProcess
import io.holunda.axon.camunda.example.process.MessageBasedTravelProcessWithCompensation
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.variable.Variables
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class ProcessStarter(private val runtime: RuntimeService) {

  @PostMapping("/travel-message")
  fun startTravelMessageBased(): ResponseEntity<String> {

    val reservation = Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "Astoria", "LH-124")

    val instanceId = runtime
      .startProcessInstanceByKey(MessageBasedTravelProcess.KEY,
        Variables.createVariables().putValue(MessageBasedTravelProcess.Variables.RESERVATION, reservation)
      )
      .processInstanceId

    return ResponseEntity.ok(instanceId)
  }

  @PostMapping("/travel-message-compensation")
  fun startTravelMessageBasedWithCompensation(): ResponseEntity<String> {

    val reservation = Reservation("piggy", LocalDate.now(), LocalDate.now().plusDays(2), "Astoria", "LH-DOES-NOT-EXIST")

    val instanceId = runtime
      .startProcessInstanceByKey(MessageBasedTravelProcessWithCompensation.KEY,
        Variables.createVariables().putValue(MessageBasedTravelProcessWithCompensation.Variables.RESERVATION, reservation)
      )
      .processInstanceId

    return ResponseEntity.ok(instanceId)
  }

}
