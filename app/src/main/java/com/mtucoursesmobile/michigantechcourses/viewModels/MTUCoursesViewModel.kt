package com.mtucoursesmobile.michigantechcourses.viewModels

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.api.getMTUBuildings
import com.mtucoursesmobile.michigantechcourses.api.getMTUCourses
import com.mtucoursesmobile.michigantechcourses.api.getMTUInstructors
import com.mtucoursesmobile.michigantechcourses.api.getMTUSections
import com.mtucoursesmobile.michigantechcourses.api.getMTUSemesters
import com.mtucoursesmobile.michigantechcourses.api.updateMTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import kotlinx.coroutines.Dispatchers
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

  val courseList = mutableStateMapOf<String, MTUCourses>()
  val sectionList = mutableStateMapOf<String, MutableList<MTUSections>>()
  val instructorList = mutableStateMapOf<Number, MTUInstructor>()
  val buildingList = mutableStateMapOf<String, MTUBuilding>()

  private val semesterList = mutableStateListOf<CurrentSemester>()
  val courseNotFound = mutableStateOf(false)

  val filteredCourseList = mutableStateMapOf<String, MTUCourses>()
  var courseSearchValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)
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

  private fun setSemester(newSemester: CurrentSemester, context: Context) {
    courseList.clear()
    courseNotFound.value = false
    currentSemester = newSemester
    viewModelScope.launch(Dispatchers.IO) {
      getMTUCourses(
        courseList,
        courseNotFound,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince,
        currentSemester
      )
      getMTUSections(
        sectionList,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince,
        context,
        currentSemester
      )
    }

  }

  fun initialCourselist(context: Context) {
    courseNotFound.value = false
    viewModelScope.launch(Dispatchers.IO) {
      getMTUBuildings(buildingList)
      getMTUInstructors(
        instructorList,
        context,
        lastUpdatedSince
      )
      getMTUSemesters(
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
      getMTUCourses(
        courseList,
        courseNotFound,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        currentSemester
      )
      getMTUSections(
        sectionList,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        context,
        currentSemester
      )
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  fun updateSemester(context: Context, loading: PullToRefreshState?) {
    courseNotFound.value = false
    viewModelScope.launch(Dispatchers.IO) {
      updateMTUCourses(
        courseList,
        sectionList,
        instructorList,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        loading,
        context,
        currentSemester
      )
    }
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

  val otherCourseFilters = mutableListOf(
    Pair(
      "Has Seats",
      mutableStateOf(false)
    )
  )

  val sortingTypes = mutableStateMapOf(
    Pair(
      "Subject & Level",
      "ascending"
    ),
    Pair(
      "Credits",
      "ascending"
    )
  )

  val sortingMode = mutableStateOf(
    Pair(
      "Subject & Level",
      "ascending"
    )
  )

  fun updateFilteredList() {
    viewModelScope.launch {
      filteredCourseList.clear()
      filteredCourseList.putAll(courseList)
      //Level
      if (courseLevelFilter.value != 1f..4f) {
        when (courseLevelFilter.value) {
          1f..1f -> {
            with(filteredCourseList.iterator()) {
              forEach { if (!(it.value.crse.first().toString().toFloat() <= 1.0)) remove() }
            }
          }

          4f..4f -> {
            with(filteredCourseList.iterator()) {
              forEach { if (!(it.value.crse.first().toString().toFloat() >= 4.0)) remove() }
            }
          }

          else -> {
            with(filteredCourseList.iterator()) {
              forEach {
                if (!courseLevelFilter.value.contains(
                    it.value.crse.first().toString().toFloat()
                  )
                ) remove()
              }
            }
          }
        }
      }
      //Credit
      if (courseCreditFilter.value != 0f..4f) {
        when (courseCreditFilter.value) {
          0f..0f -> {
            with(filteredCourseList.iterator()) {
              forEach { if (!(it.value.maxCredits <= 1.0)) remove() }
            }
          }

          4f..4f -> {
            with(filteredCourseList.iterator()) {
              forEach { if (!(it.value.maxCredits >= 4.0)) remove() }
            }
          }

          else -> {
            with(filteredCourseList.iterator()) {
              forEach { if (!courseCreditFilter.value.contains(it.value.maxCredits) || !courseCreditFilter.value.contains(it.value.minCredits)) remove() }
            }
          }
        }
      }
      //Other
      if (courseOtherFilter.isNotEmpty()) {
        for (other in courseOtherFilter) {
          when (other) {
            "Has Seats" -> {
              with(filteredCourseList.iterator()) {
                forEach {
                  if (sectionList[it.key]?.none { section -> section.availableSeats.toFloat() > 0 } == true) {
                    remove()
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}