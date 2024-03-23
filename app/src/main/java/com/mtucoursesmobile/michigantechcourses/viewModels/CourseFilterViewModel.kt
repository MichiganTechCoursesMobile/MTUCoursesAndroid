package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses

class CourseFilterViewModel : ViewModel() {
  var searchBarValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)
  val typeFilter = mutableStateListOf<String>()
  val levelFilter = mutableStateListOf<String>()
  val creditFilter = mutableStateListOf<String>()
  val otherFilter = mutableStateListOf<String>()

  fun toggleType(value: String) {
    if (typeFilter.isEmpty()) {
      typeFilter.add(value)
      return
    }
    if (typeFilter.contains(value)) {
      typeFilter.remove(value)
      return
    }
    typeFilter.add(value)
  }

  fun toggleLevel(value: String) {
    if (levelFilter.isEmpty()) {
      levelFilter.add(value.first().toString())
      return
    }
    if (levelFilter.contains(value.first().toString())) {
      levelFilter.remove(value.first().toString())
      return
    }
    levelFilter.add(value.first().toString())
  }

  fun toggleCredit(value: String) {
    if (creditFilter.isEmpty()) {
      creditFilter.add(value)
      return
    }
    if (creditFilter.contains(value)) {
      creditFilter.remove(value)
      return
    }
    creditFilter.add(value)
  }

  fun toggleOther(value: String) {
    if (otherFilter.isEmpty()) {
      otherFilter.add(value)
      return
    }
    if (otherFilter.contains(value)) {
      otherFilter.remove(value)
      return
    }
    otherFilter.add(value)
  }

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