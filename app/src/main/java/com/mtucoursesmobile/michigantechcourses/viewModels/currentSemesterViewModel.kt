package com.mtucoursesmobile.michigantechcourses.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.api.getSemesterCourses
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class currentSemesterViewModel : ViewModel() {
  var currentSemester =
    CurrentSemester(
      "Fall 2024",
      "2024",
      "FALL"
    )
  val courseList = mutableStateListOf<MTUCourses>()
  val alreadyRan = mutableStateOf(false)

  fun setSemester(newSemester: CurrentSemester, db: AppDatabase, context: Context) {
    currentSemester = newSemester
    viewModelScope.launch(Dispatchers.IO) {
      getSemesterCourses(
        courseList,
        context,
        newSemester.semester,
        newSemester.year,
        db
      )
    }
  }

  fun initialCourselist(db: AppDatabase, context: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      getSemesterCourses(
        courseList,
        context,
        currentSemester.semester,
        currentSemester.year,
        db
      )
    }
  }
}