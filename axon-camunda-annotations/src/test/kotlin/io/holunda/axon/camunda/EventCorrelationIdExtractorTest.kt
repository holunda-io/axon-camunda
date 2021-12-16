package io.holunda.axon.camunda

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class EventCorrelationIdExtractorTest {

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
    Assertions.assertThatThrownBy { extractCorrelationId(EventWithTwoCorrelationIds("foo", "bar")) }
      .withFailMessage("Found more than one fields marked with @EventCorrelationId annotation.")
      .isExactlyInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun `should find one from base class`() {
    val event = BaseEvent()
    event.value = "foo"
    assertThat(extractCorrelationId(event)).isNotNull
  }

  // FIXME support inheritance
  @Disabled
  @Test
  fun `should find one from super class`() {
    val event = ExtendedEvent()
    event.another = "hmm"
    event.value = "foo"
    assertThat(extractCorrelationId(event)).isNotNull
  }

  // FIXME support inheritance
  @Disabled
  @Test
  fun `should find multiple from base and super class`() {
    val event = OverridingEvent()
    event.another = "another"
    event.value = "value"

    Assertions.assertThatThrownBy { extractCorrelationId(event) }
      .hasCauseInstanceOf(IllegalArgumentException::class.java)
      .withFailMessage("Found more than one fields marked with @EventCorrelationId annotation.")
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
