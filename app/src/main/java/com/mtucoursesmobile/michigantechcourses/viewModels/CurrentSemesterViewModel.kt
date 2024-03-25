package com.mtucoursesmobile.michigantechcourses.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.api.getSemesterCourses
import com.mtucoursesmobile.michigantechcourses.api.updateSemesterCourses
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentSemesterViewModel : ViewModel() {
  var currentSemester =
    CurrentSemester(
      "Fall 2024",
      "2024",
      "FALL"
    )
  val courseList = mutableStateListOf<MTUCoursesEntry>()

  val lastUpdatedSince = mutableListOf<LastUpdatedSince>()

  fun setSemester(newSemester: CurrentSemester, context: Context) {
    currentSemester = newSemester
    viewModelScope.launch(Dispatchers.IO) {
      getSemesterCourses(
        courseList,
        context,
        newSemester.semester,
        newSemester.year,
        lastUpdatedSince
      )
    }
  }

  fun initialCourselist(context: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      getSemesterCourses(
        courseList,
        context,
        currentSemester.semester,
        currentSemester.year,
        lastUpdatedSince
      )
    }
  }

  fun updateSemester(semester: CurrentSemester, context: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      updateSemesterCourses(
        courseList,
        context,
        semester.semester,
        semester.year,
        lastUpdatedSince
      )
    }
  }
}