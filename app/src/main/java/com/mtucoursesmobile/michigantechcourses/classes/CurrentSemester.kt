package com.mtucoursesmobile.michigantechcourses.classes

data class CurrentSemester(
  val readable: String,
  val year: String,
  val semester: String
)

val semesterList = arrayListOf(
  CurrentSemester(
    "Fall 2024",
    "2024",
    "FALL"
  ),
  CurrentSemester(
    "Spring 2024",
    "2024",
    "SPRING"
  ),
  CurrentSemester(
    "Summer 2024",
    "2024",
    "SUMMER"
  )
)