package io.holunda.axon.camunda.example.travel.compensation

import io.holunda.axon.camunda.example.travel.compensation.process.MessageBasedTravelProcessWithCompensation
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration
import org.junit.Rule
import org.junit.Test

class ProcessTest {

  @get:Rule
  val camunda = StandaloneInMemoryTestConfiguration().rule()

  @Test
  @Deployment(resources = arrayOf(MessageBasedTravelProcessWithCompensation.BPMN))
  fun `should deploy`() {
    // no code
  }
}
