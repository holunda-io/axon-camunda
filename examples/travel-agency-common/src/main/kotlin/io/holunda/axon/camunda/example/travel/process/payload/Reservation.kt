package io.holunda.axon.camunda.example.travel.process.payload

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.util.*

data class Reservation(
  val name: String,
  @get: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val from: LocalDate,
  @get: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  val to: LocalDate,
  val hotel: String,
  val flightNumber: String,
  val id: String = UUID.randomUUID().toString()
)
