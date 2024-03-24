package com.mtucoursesmobile.michigantechcourses.viewModels

import android.util.Range
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses

class CourseFilterViewModel : ViewModel() {
  var searchBarValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)
  val typeFilter = mutableStateListOf<String>()
  val levelFilter = mutableStateOf(1f..4f)
  val creditFilter = mutableStateOf(0f..4f)

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

  fun toggleLevel(value: ClosedFloatingPointRange<Float>) {
    levelFilter.value = value
  }

  fun toggleCredit(value: ClosedFloatingPointRange<Float>) {
    creditFilter.value = value
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