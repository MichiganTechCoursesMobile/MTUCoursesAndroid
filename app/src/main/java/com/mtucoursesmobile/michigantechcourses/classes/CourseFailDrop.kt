package com.mtucoursesmobile.michigantechcourses.classes

data class CourseFailDrop(
  val semester: String,
  val year: Number,
  val dropped: Number,
  val failed: Number,
  val total: Number
)
