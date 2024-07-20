package com.mtucoursesmobile.michigantechcourses.classes

import java.time.LocalDate

data class CalendarEntry(
  val day: String,
  val startHour: Int,
  val endHour: Int,
  val startMinute: Int,
  val endMinute: Int,
  val startDay: Int,
  val startMonth: Int,
  val startYear: Int,
  val endDay: Int,
  val endMonth: Int,
  val endYear: Int,
  val section: MTUSections
)