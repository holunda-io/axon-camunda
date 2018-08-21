package io.holunda.axon.camunda.example.rest

import io.holunda.axon.camunda.example.TravelProcess
import org.camunda.bpm.engine.RuntimeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProcessStarter(private val runtime: RuntimeService) {

  @PostMapping("/travel")
  fun startTravel(): ResponseEntity<String> {
    return ResponseEntity.ok(runtime.startProcessInstanceByKey(TravelProcess.KEY).processInstanceId)
  }

}
