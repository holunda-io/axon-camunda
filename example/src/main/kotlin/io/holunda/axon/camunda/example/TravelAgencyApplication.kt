package io.holunda.axon.camunda.example

import io.holunda.axon.camunda.EnableAxonCamunda
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneOffset

fun main(args: Array<String>) {
  SpringApplication.run(TravelAgencyApplication::class.java, *args)
}

@SpringBootApplication
@EnableProcessApplication
@EnableAxonCamunda
@EnableTransactionManagement
class TravelAgencyApplication

fun interval(start: LocalDate, end: LocalDate) = Interval.of(start.atStartOfDay().toInstant(ZoneOffset.UTC), end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))

