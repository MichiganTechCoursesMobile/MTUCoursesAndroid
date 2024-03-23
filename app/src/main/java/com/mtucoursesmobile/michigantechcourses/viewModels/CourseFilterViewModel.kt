package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CourseFilterViewModel : ViewModel() {
  var searchBarValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)

  val courseTypes: List<Pair<String, MutableState<Boolean>>> = listOf(
    Pair(
      "CS",
      mutableStateOf(false)
    ),
    Pair(
      "BL",
      mutableStateOf(false)
    ),
    Pair(
      "ACC",
      mutableStateOf(false)
    ),
    Pair(
      "AF",
      mutableStateOf(false)
    ),
    Pair(
      "EE",
      mutableStateOf(false)
    )
  )
  val courseLevels: List<Pair<String, MutableState<Boolean>>> = listOf(
    Pair(
      "1000",
      mutableStateOf(false)
    ),
    Pair(
      "2000",
      mutableStateOf(false)
    ),
    Pair(
      "3000",
      mutableStateOf(false)
    ),
    Pair(
      "4000+",
      mutableStateOf(false)
    )
  )
  val courseCredits: List<Pair<String, MutableState<Boolean>>> = listOf(
    Pair(
      "0.5",
      mutableStateOf(false)
    ),
    Pair(
      "1.0",
      mutableStateOf(false)
    ),
    Pair(
      "2.0",
      mutableStateOf(false)
    ),
    Pair(
      "3.0",
      mutableStateOf(false)
    ),
    Pair(
      "4.0",
      mutableStateOf(false)
    )
  )
  val otherCourseFilters: List<Pair<String, MutableState<Boolean>>> = listOf(
    Pair(
      "Has Seats",
      mutableStateOf(false)
    )
  )

}