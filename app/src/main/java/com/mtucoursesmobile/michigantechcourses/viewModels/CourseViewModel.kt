package com.mtucoursesmobile.michigantechcourses.viewModels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.api.getMTUBuildings
import com.mtucoursesmobile.michigantechcourses.api.getMTUCourseDropRates
import com.mtucoursesmobile.michigantechcourses.api.getMTUCourses
import com.mtucoursesmobile.michigantechcourses.api.getMTUInstructors
import com.mtucoursesmobile.michigantechcourses.api.getMTUSections
import com.mtucoursesmobile.michigantechcourses.api.getMTUSemesters
import com.mtucoursesmobile.michigantechcourses.api.updateMTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.CourseFailDrop
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.classes.MTUSemesters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Year
import java.util.Calendar

class CourseViewModel(app: Application) :
  AndroidViewModel(app) {

  var currentSemester = initialSemester()

  val courseList = mutableStateMapOf<String, MTUCourses>()
  val sectionList = mutableStateMapOf<String, MutableList<MTUSections>>()
  val instructorList = mutableStateMapOf<Number, MTUInstructor>()
  val buildingList = mutableStateMapOf<String, MTUBuilding>()
  val failList = mutableStateMapOf<String, List<CourseFailDrop>>()

  val semesterList = mutableStateListOf<MTUSemesters>()

  val filteredCourseList = mutableStateMapOf<String, MTUCourses>()
  var courseSearchValue = mutableStateOf("")
  var showFilter = mutableStateOf(false)
  val courseLevelFilter = mutableStateOf(1f..4f)
  val courseCreditFilter = mutableStateOf(0f..4f)
  private val courseOtherFilter = mutableStateListOf<String>()

  private val lastUpdatedSince = mutableListOf<LastUpdatedSince>()

  val semesterStatus = mutableIntStateOf(0)
  val courseStatus = mutableIntStateOf(0)
  val sectionStatus = mutableIntStateOf(0)
  val instructorStatus = mutableIntStateOf(0)
  val buildingStatus = mutableIntStateOf(0)
  val dropStatus = mutableIntStateOf(0)

  val attemptedBasketImport = mutableStateOf(false)


  // Get the initial course list
  init {
    initialCourselist(
    )
  }

  private fun initialSemester(): CurrentSemester {
    var targetSemester = "FALL"
    var targetYear = Year.now().value.toString()
    if (Calendar.getInstance().get(Calendar.MONTH) + 1 > 8) {
      targetSemester = "SPRING"
      targetYear = (Year.now().value + 1).toString()
    }
    return CurrentSemester(
      "${
        targetSemester.lowercase().replaceFirstChar(Char::titlecase)
      } $targetYear",
      targetYear,
      targetSemester
    )
  }

  // Updates the selected semester's year (Fall 2024 -> Fall 2025)
  fun updateSemesterYear(
    year: Number
  ) {
    currentSemester = CurrentSemester(
      "${
        currentSemester.semester.lowercase().replaceFirstChar(Char::titlecase)
      } $year",
      year.toString(),
      currentSemester.semester
    )
    setSemester(
      currentSemester
    )
  }

  // Change the semester but keep the year (Fall 2024 -> Spring 2024)
  fun updateSemesterPeriod(
    semester: String
  ) {
    currentSemester = CurrentSemester(
      "$semester ${currentSemester.year}",
      currentSemester.year,
      semester.uppercase()
    )
    setSemester(
      currentSemester
    )
  }

  // Set the semester to a given semester, then get its courses and sections
  fun setSemester(
    newSemester: CurrentSemester
  ) {
    val context = getApplication<Application>().applicationContext
    courseStatus.intValue = 0
    sectionStatus.intValue = 0
    courseList.clear()
    sectionList.clear()
    filteredCourseList.clear()
    currentSemester = newSemester
    viewModelScope.launch(Dispatchers.IO) {
      getMTUCourses(
        courseList,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince,
        currentSemester,
        courseStatus,
        context
      )
      getMTUSections(
        sectionList,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince,
        currentSemester,
        sectionStatus,
        context
      )
    }

  }

  // Get the first set of courses, sections, instructors, buildings, semesters, and drop rates
  private fun initialCourselist() {
    viewModelScope.launch(Dispatchers.IO) {
      val context = getApplication<Application>().applicationContext
      getMTUBuildings(
        buildingList,
        buildingStatus,
        context
      )
      getMTUInstructors(
        instructorList,
        lastUpdatedSince,
        instructorStatus,
        context
      )
      getMTUSemesters(
        semesterList,
        semesterStatus,
        context
      )
      getMTUCourseDropRates(
        failList,
        dropStatus,
        context
      )

      currentSemester = initialSemester()
      getMTUCourses(
        courseList,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        currentSemester,
        courseStatus,
        context
      )
      getMTUSections(
        sectionList,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince,
        currentSemester,
        sectionStatus,
        context
      )
    }
  }

  // Update the semester's courses (usually every 30 seconds or so)
  fun updateSemester(
    context: Context,
    loading: MutableState<Boolean>?
  ) {
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

  fun retry() {
    val context = getApplication<Application>().applicationContext
    if (courseStatus.intValue == 2) {
      courseStatus.intValue = 0
      filteredCourseList.clear()
      viewModelScope.launch(Dispatchers.IO) {
        getMTUCourses(
          courseList,
          currentSemester.semester,
          currentSemester.year,
          lastUpdatedSince,
          currentSemester,
          courseStatus,
          context
        )
      }
    }
    if (sectionStatus.intValue == 2) {
      sectionStatus.intValue = 0
      viewModelScope.launch(Dispatchers.IO) {
        getMTUSections(
          sectionList,
          currentSemester.semester,
          currentSemester.year,
          lastUpdatedSince,
          currentSemester,
          sectionStatus,
          context
        )
      }
    }
    if (semesterStatus.intValue == 2) {
      semesterStatus.intValue = 0
      getMTUSemesters(
        semesterList,
        semesterStatus,
        context
      )
    }
    if (instructorStatus.intValue == 2) {
      instructorStatus.intValue = 0
      viewModelScope.launch(Dispatchers.IO) {
        getMTUInstructors(
          instructorList,
          lastUpdatedSince,
          instructorStatus,
          context
        )
      }
    }
    if (buildingStatus.intValue == 2) {
      buildingStatus.intValue = 0
      viewModelScope.launch(Dispatchers.IO) {
        getMTUBuildings(
          buildingList,
          buildingStatus,
          context
        )
      }
    }
    if (dropStatus.intValue == 2) {
      dropStatus.intValue = 0
      viewModelScope.launch(Dispatchers.IO) {
        getMTUCourseDropRates(
          failList,
          dropStatus,
          context
        )
      }
    }
  }

  // Sort by Subject & Level switch
  fun toggleLevel(value: ClosedFloatingPointRange<Float>) {
    courseLevelFilter.value = value
    updateFilteredList()
  }

  // Sort by Credit count switch
  fun toggleCredit(value: ClosedFloatingPointRange<Float>) {
    courseCreditFilter.value = value
    updateFilteredList()
  }

  // Toggle has seats (or any future filter)
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

  // Update the filtered list based on the current filter settings
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
              forEach {
                if (!courseCreditFilter.value.contains(it.value.maxCredits) || !courseCreditFilter.value.contains(
                    it.value.minCredits
                  )
                ) remove()
              }
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