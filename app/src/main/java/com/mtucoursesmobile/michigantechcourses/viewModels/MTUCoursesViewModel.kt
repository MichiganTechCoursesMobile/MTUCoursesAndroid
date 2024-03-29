package com.mtucoursesmobile.michigantechcourses.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.api.getInstructors
import com.mtucoursesmobile.michigantechcourses.api.getSemesterCourses
import com.mtucoursesmobile.michigantechcourses.api.getSemesters
import com.mtucoursesmobile.michigantechcourses.api.updateSemesterCourses
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Year
import java.util.Calendar

class MTUCoursesViewModel : ViewModel() {
  var currentSemester =
    CurrentSemester(
      "Fall 2024",
      "2024",
      "FALL"
    )

  val courseList = mutableStateListOf<MTUCoursesEntry>()
  private val semesterList = mutableStateListOf<CurrentSemester>()
  val courseInstructorList = mutableStateListOf<MTUInstructor>()
  val courseNotFound = mutableStateOf(false)

  val filteredCourseList = mutableStateListOf<MTUCoursesEntry>()
  var courseSearchValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)
  private val courseTypeFilter = mutableStateListOf<String>()
  val courseLevelFilter = mutableStateOf(1f..4f)
  val courseCreditFilter = mutableStateOf(0f..4f)
  private val courseOtherFilter = mutableStateListOf<String>()

  private val lastUpdatedSince = mutableListOf<LastUpdatedSince>()

  @OptIn(ExperimentalMaterial3Api::class)
  fun updateSemesterYear(year: Number, context: Context) {
    currentSemester = CurrentSemester(
      "${
        currentSemester.semester.lowercase().replaceFirstChar(Char::titlecase)
      } $year",
      year.toString(),
      currentSemester.semester
    )
    setSemester(
      currentSemester,
      context
    )
  }

  @OptIn(ExperimentalMaterial3Api::class)
  fun updateSemesterPeriod(semester: String, context: Context) {
    currentSemester = CurrentSemester(
      "$semester ${currentSemester.year}",
      currentSemester.year,
      semester.uppercase()
    )
    setSemester(
      currentSemester,
      context
    )
  }

  fun updateCourseTypes() {
    val tempCourseTypeList = courseList.distinctBy { course -> course.entry.course[0].subject }
    courseTypes.clear()
    for (course in tempCourseTypeList) {
      courseTypes.add(
        Pair(
          course.entry.course[0].subject,
          mutableStateOf(false)
        )
      )
    }
  }

  private fun setSemester(newSemester: CurrentSemester, context: Context) {
    courseList.clear()
    courseNotFound.value = false
    currentSemester = newSemester
    viewModelScope.launch(Dispatchers.IO) {
      getSemesterCourses(
        courseList,
        courseNotFound,
        context,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince
      )
    }
  }

  fun initialCourselist(context: Context) {
    courseNotFound.value = false
    viewModelScope.launch(Dispatchers.IO) {
      getInstructors(
        courseInstructorList,
        context
      )
      getSemesters(
        semesterList,
        context
      )
      var targetSemester = "FALL"
      var targetYear = Year.now().value.toString()
      if (Calendar.getInstance().get(Calendar.MONTH) + 1 > 8) {
        targetSemester = "SPRING"
        targetYear = (Year.now().value + 1).toString()
      }
      currentSemester = CurrentSemester(
        "${
          targetSemester.lowercase().replaceFirstChar(Char::titlecase)
        } $targetYear",
        targetYear,
        targetSemester
      )
      getSemesterCourses(
        courseList,
        courseNotFound,
        context,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince
      )
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  fun updateSemester(context: Context, loading: PullToRefreshState?) {
    courseNotFound.value = false
    viewModelScope.launch(Dispatchers.IO) {
      updateSemesterCourses(
        courseList,
        context,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        loading
      )
    }
  }

  fun toggleType(value: String) {
    if (courseTypeFilter.isEmpty()) {
      courseTypeFilter.add(value)
      updateFilteredList()
      return
    }
    if (courseTypeFilter.contains(value)) {
      courseTypeFilter.remove(value)
      updateFilteredList()
      return
    }
    courseTypeFilter.add(value)
    updateFilteredList()
  }

  fun toggleLevel(value: ClosedFloatingPointRange<Float>) {
    courseLevelFilter.value = value
    updateFilteredList()
  }

  fun toggleCredit(value: ClosedFloatingPointRange<Float>) {
    courseCreditFilter.value = value
    updateFilteredList()
  }

  fun toggleOther(value: String) {
    if (courseOtherFilter.isEmpty()) {
      courseOtherFilter.add(value)
      updateFilteredList()
      return
    }
    if (courseOtherFilter.contains(value)) {
      courseOtherFilter.remove(value)
      updateFilteredList()
      return
    }
    courseOtherFilter.add(value)
    updateFilteredList()
  }

  val courseTypes = mutableListOf<Pair<String, MutableState<Boolean>>>()

  val otherCourseFilters: List<Pair<String, MutableState<Boolean>>> = listOf(
    Pair(
      "Has Seats",
      mutableStateOf(false)
    )
  )

  fun updateFilteredList() {
    Log.d(
      "DEBUG",
      "I am updating Filters"
    )
    viewModelScope.launch {
      filteredCourseList.clear()
      filteredCourseList.addAll(courseList)
      //Type
      if (courseTypeFilter.isNotEmpty()) {
        filteredCourseList.removeAll(courseList.filter { course -> course.entry.course[0].subject !in courseTypeFilter })
      }

      //Level
      if (courseLevelFilter.value != 1f..4f) {
        when (courseLevelFilter.value) {
          1f..1f -> {
            filteredCourseList.removeAll(courseList.filter { course ->
              (!(course.entry.course[0].crse.first().toString().toFloat() <= 1.0))
            })
          }

          4f..4f -> {
            filteredCourseList.removeAll(courseList.filter { course ->
              (!(course.entry.course[0].crse.first().toString().toFloat() >= 4.0))
            })
          }

          else -> {
            filteredCourseList.removeAll(courseList.filter { course ->
              !courseLevelFilter.value.contains(
                course.entry.course[0].crse.first().toString().toFloat()
              )
            })
          }
        }
      }
      //Credit
      if (courseCreditFilter.value != 0f..4f) {
        when (courseCreditFilter.value) {
          0f..0f -> {
            filteredCourseList.removeAll(courseList.filter { course -> (!(course.entry.course[0].maxCredits <= 1.0)) })
          }

          4f..4f -> {
            filteredCourseList.removeAll(courseList.filter { course -> (!(course.entry.course[0].maxCredits >= 4.0)) })
          }

          else -> {
            filteredCourseList.removeAll(courseList.filter { course ->
              (!courseCreditFilter.value.contains(course.entry.course[0].maxCredits) || !courseCreditFilter.value.contains(course.entry.course[0].minCredits))
            })
          }
        }
      }
      //Other
      if (courseOtherFilter.isNotEmpty()) {
        for (other in courseOtherFilter) {
          when (other) {
            "Has Seats" -> {
              filteredCourseList.removeAll(courseList.filter { course ->
                course.entry.sections.none { section -> section?.availableSeats?.toFloat()!! > 0 }
              })
            }
          }
        }
      }
    }
  }
}