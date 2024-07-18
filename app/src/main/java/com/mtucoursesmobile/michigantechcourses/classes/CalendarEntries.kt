package com.mtucoursesmobile.michigantechcourses.classes

data class CalendarEntries(
  val day: String,
  val entries: Map<Int, List<MTUSections>?>?
)