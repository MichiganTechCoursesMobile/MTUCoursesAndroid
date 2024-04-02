package com.mtucoursesmobile.michigantechcourses

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
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

    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
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
        MainView(
          coursesViewModel,
          basketViewModel,
          db
        )
      }
    }
  }
}


