package com.mtucoursesmobile.michigantechcourses.classes

data class CourseBasket(
  val id: String,
  val name: String,
  val sections: MutableMap<String, MTUSections>
)
