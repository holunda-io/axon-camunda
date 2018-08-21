package io.holunda.axon.camunda.example

import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneOffset

fun interval(start: LocalDate, end: LocalDate) = Interval.of(start.atStartOfDay().toInstant(ZoneOffset.UTC), end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
