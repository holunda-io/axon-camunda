package de.holisticon.axon.camunda.example

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ProcessTest {

  @get:Rule
  val camunda = StandaloneInMemoryTestConfiguration().rule()

  @Test
  @Deployment(resources = arrayOf(TravelProcess.BPMN))
  fun `should deploy`() {
    // no code
  }
}
