package com.mtucoursesmobile.michigantechcourses.classes

data class CourseFailDrop(
  val semester: String,
  val year: Int,
  val dropped: Float,
  val failed: Float,
  val total: Float
)
