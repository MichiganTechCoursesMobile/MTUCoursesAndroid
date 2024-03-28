package com.mtucoursesmobile.michigantechcourses.viewModels

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
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
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Year
import java.util.Calendar

class CurrentSemesterViewModel : ViewModel() {
  var currentSemester =
    CurrentSemester(
      "Fall 2024",
      "2024",
      "FALL"
    )
  val courseList = mutableStateListOf<MTUCoursesEntry>()
  val semesterList = mutableStateListOf<CurrentSemester>()
  val instructorList = mutableStateListOf<MTUInstructor>()
  val courseNotFound = mutableStateOf(false)

  val lastUpdatedSince = mutableListOf<LastUpdatedSince>()

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

  fun setSemester(newSemester: CurrentSemester, context: Context) {
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
        instructorList,
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
}