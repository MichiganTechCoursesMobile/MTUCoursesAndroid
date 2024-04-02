package com.mtucoursesmobile.michigantechcourses.classes

data class CourseBasket(
  val id: String,
  var name: String,
  val sections: MutableMap<String, MTUSections>
)
