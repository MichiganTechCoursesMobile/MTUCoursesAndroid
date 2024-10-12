package com.mtucoursesmobile.michigantechcourses

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import com.mtucoursesmobile.michigantechcourses.views.MainView
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT
      ),
      navigationBarStyle = SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT
      )
    )

    setContent {
      var isLoading by remember { mutableStateOf(false) }
      LaunchedEffect(Unit) {
        // I also hate this, but it prevents flash bangs when loading the app
        delay(250)
        isLoading = true
      }

      val content: View = findViewById(android.R.id.content)
      content.viewTreeObserver.addOnPreDrawListener(
        object : ViewTreeObserver.OnPreDrawListener {
          override fun onPreDraw(): Boolean {
            return if (isLoading) {
              content.viewTreeObserver.removeOnPreDrawListener(this)
              true
            } else {
              false
            }
          }
        }
      )
      val courseViewModel: CourseViewModel = viewModel()
      val basketViewModel: BasketViewModel = viewModel()
      val courses = courseViewModel.courseList.toList().toMutableStateList()
      val context = LocalContext.current

      LaunchedEffect(courses) {
        courseViewModel.updateFilteredList()
      }

      LaunchedEffect(Unit) {
        while (true) {
          if (courseViewModel.courseStatus.intValue == 0 && courseViewModel.courseList.isNotEmpty()) {
            courseViewModel.updateSemester(
              context,
              null
            )
          }
          delay(30000)
        }
      }

      MichiganTechCoursesTheme {
        MainView(
          courseViewModel,
          basketViewModel
        )
      }
    }
  }
}


