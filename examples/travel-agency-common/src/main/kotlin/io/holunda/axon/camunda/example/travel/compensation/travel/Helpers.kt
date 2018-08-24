package io.holunda.axon.camunda.example.travel.compensation.travel

import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

fun interval(start: LocalDate, end: LocalDate) = Interval.of(start.atStartOfDay().toInstant(ZoneOffset.UTC), end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
fun start(interval: Interval) = interval.start.atZone(ZoneId.systemDefault()).toLocalDate()
fun end(interval: Interval) = interval.end.atZone(ZoneId.systemDefault()).toLocalDate()
