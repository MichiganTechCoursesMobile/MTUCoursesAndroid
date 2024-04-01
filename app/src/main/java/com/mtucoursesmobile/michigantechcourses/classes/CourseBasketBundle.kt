package com.mtucoursesmobile.michigantechcourses.classes

data class CourseBasketBundle(
  val semester: Pair<String, String>,
  val baskets: MutableList<CourseBasket>
)
