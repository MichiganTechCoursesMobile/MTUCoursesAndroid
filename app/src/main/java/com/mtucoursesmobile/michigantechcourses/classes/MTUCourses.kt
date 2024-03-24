package com.mtucoursesmobile.michigantechcourses.classes

data class MTUCourses(
  val id: String, val year: Int, val semester: String, val subject: String, val crse: String,
  val title: String, val description: String, val updatedAt: String, val deletedAt: String,
  val prereqs: String, val offered: Array<String>, val minCredits: Double, val maxCredits: Double
)

data class MTUCourseSectionBundle(
  val course: MTUCourses,
  val sections: List<MTUSections>
)