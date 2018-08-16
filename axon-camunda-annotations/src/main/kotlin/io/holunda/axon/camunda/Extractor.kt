package io.holunda.axon.camunda

import kotlin.reflect.full.declaredMemberProperties



fun extractCorrelationId(instance: Any): Any? {

  val values = extract(instance, EventCorrelationId::class.java)
  return when (values.size) {
    0 -> null
    1 -> values[0]
    else -> throw IllegalArgumentException("Found more than one fields marked with @EventCorrelationId annotation.")
  }
}


private fun extract(instance: Any, annotationClass: Class<out Annotation>): List<Any> = instance.javaClass.declaredFields
  .filter { it.annotations.count { it.javaClass == annotationClass } > 0 }
  .map { it.get(instance) }
  .union(
    instance.javaClass.declaredMethods
      .filter { it.parameterCount == 0 && it.name.startsWith("get") && it.isAnnotationPresent(annotationClass) }
      .map { it.invoke(instance) })
  .union(
    instance.javaClass.kotlin.declaredMemberProperties
      .filter { it.annotations.filter { it.annotationClass.java == annotationClass }.isNotEmpty() }
      .map { it.invoke(instance) as Any })
  .toList()

