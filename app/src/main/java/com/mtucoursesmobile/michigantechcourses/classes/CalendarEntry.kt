package com.mtucoursesmobile.michigantechcourses.classes

import java.time.LocalDate

data class CalendarEntry(
  val day: String,
  val startHour: Int,
  val endHour: Int,
  val startMinute: Int,
  val endMinute: Int,
  val startDate: LocalDate,
  val endDate: LocalDate,
  val section: MTUSections
)