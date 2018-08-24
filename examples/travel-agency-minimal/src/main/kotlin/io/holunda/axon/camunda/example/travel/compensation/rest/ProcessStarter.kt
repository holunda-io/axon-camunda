package io.holunda.axon.camunda.example.travel.compensation.rest

import io.holunda.axon.camunda.example.travel.compensation.process.MessageBasedTravelProcess
import io.holunda.spring.io.holunda.axon.camunda.example.process.Reservation
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.variable.Variables
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class ProcessStarter(private val runtime: RuntimeService) {

  @PostMapping("/travel")
  fun startTravelMessageBased(): ResponseEntity<String> {

    val reservation = Reservation("kermit", LocalDate.now(), LocalDate.now().plusDays(2), "Astoria", "LH-124")

    val instanceId = runtime
      .startProcessInstanceByKey(MessageBasedTravelProcess.KEY,
        Variables.createVariables().putValue(MessageBasedTravelProcess.Variables.RESERVATION, reservation)
      )
      .processInstanceId

    return ResponseEntity.ok(instanceId)
  }

}
