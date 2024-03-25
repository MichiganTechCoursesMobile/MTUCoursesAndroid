package com.mtucoursesmobile.michigantechcourses.classes

data class MTUCoursesEntry(
  val semester: String,
  val year: String,
  val courseId: String,
  val entry: MTUCourseSectionBundle
)
