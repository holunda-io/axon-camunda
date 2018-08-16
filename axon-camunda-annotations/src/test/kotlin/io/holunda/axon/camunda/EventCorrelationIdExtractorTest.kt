package io.holunda.axon.camunda

import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class EventCorrelationIdExtractorTest {

  @get: Rule
  val thrown = ExpectedException.none()

  @Test
  fun `should find none`() {
    assertThat(extractCorrelationId(NoIdEvent("foo"))).isNull()
  }

  @Test
  fun `should find one`() {
    assertThat(extractCorrelationId(EventWithOneCorrelationId("foo"))).isNotNull
  }

  @Test
  fun `should find multiple`() {
    thrown.expectMessage("Found more than one fields marked with @EventCorrelationId annotation.")
    thrown.expect(IllegalArgumentException::class.java)
    extractCorrelationId(EventWithTwoCorrelationIds("foo", "bar"))
  }

  @Test
  fun `should find one from base class`() {
    val event = BaseEvent()
    event.value = "foo"
    assertThat(extractCorrelationId(event)).isNotNull
  }

  // FIXME support inheritance
  @Ignore
  @Test
  fun `should find one from super class`() {
    val event = ExtendedEvent()
    event.another = "hmm"
    event.value = "foo"
    assertThat(extractCorrelationId(event)).isNotNull
  }

  // FIXME support inheritance
  @Ignore
  @Test
  fun `should find multiple from base and super class`() {
    thrown.expectMessage("Found more than one fields marked with @EventCorrelationId annotation.")
    thrown.expect(IllegalArgumentException::class.java)
    val event = OverridingEvent()
    event.another = "another"
    event.value = "value"
    assertThat(extractCorrelationId(event)).isNull()
  }

}

data class NoIdEvent(val value: String)
data class EventWithOneCorrelationId(@EventCorrelationId val value: String)
data class EventWithTwoCorrelationIds(@EventCorrelationId val value: String, @EventCorrelationId val another: String)

open class BaseEvent {
  @EventCorrelationId
  lateinit var value: String
}

class ExtendedEvent : BaseEvent() {
  lateinit var another: String
}

class ValueAndMethodEvent : BaseEvent() {

  lateinit var hidden: String

  @EventCorrelationId
  fun getStrange() = hidden
}

class OverridingEvent : BaseEvent() {
  @EventCorrelationId
  lateinit var another: String
}
