package com.mtucoursesmobile.michigantechcourses.classes

data class CourseBasket(
  val name: String,
  val sections : MutableMap<String, MTUSections>
)
