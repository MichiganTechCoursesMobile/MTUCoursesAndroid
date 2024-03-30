package com.mtucoursesmobile.michigantechcourses

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import com.mtucoursesmobile.michigantechcourses.views.MainView


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

        // Initialized the local storage DB
//        val db = remember {
//          Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "mtucourses-db"
//          ).addTypeConverter(MTUCoursesConverter()).build()
//        }
        val courses = coursesViewModel.courseList.toList().toMutableStateList()
        LaunchedEffect(Unit) {
          coursesViewModel.initialCourselist(
            context
          )
        }
        LaunchedEffect(courses) {
          coursesViewModel.updateFilteredList()
        }
        MainView(
          coursesViewModel
        )
      }
    }
  }
}


