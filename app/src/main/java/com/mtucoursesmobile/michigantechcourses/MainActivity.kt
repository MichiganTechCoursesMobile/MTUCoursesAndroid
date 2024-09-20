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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketConverters
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import com.mtucoursesmobile.michigantechcourses.views.MainView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
      val context = LocalContext.current
      val coursesViewModel: MTUCoursesViewModel = viewModel()
      val basketViewModel: BasketViewModel = viewModel()
      val scope = rememberCoroutineScope()
      val db = remember {
        Room.databaseBuilder(
          context,
          BasketDB::class.java,
          "basket-db"
        ).addTypeConverter(BasketConverters()).build()
      }
      val courses = coursesViewModel.courseList.toList().toMutableStateList()

      LaunchedEffect(Unit) {
        coursesViewModel.initialCourselist(
          context
        )
        scope.launch {
          basketViewModel.getSemesterBaskets(
            coursesViewModel.currentSemester,
            db
          )
        }
      }
      LaunchedEffect(courses) {
        coursesViewModel.updateFilteredList()
      }

      MichiganTechCoursesTheme {
        MainView(
          coursesViewModel,
          basketViewModel,
          db
        )
      }
    }
  }
}


