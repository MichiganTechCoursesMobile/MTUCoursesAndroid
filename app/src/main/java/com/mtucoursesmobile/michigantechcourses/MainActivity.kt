package com.mtucoursesmobile.michigantechcourses

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
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
        val semesterViewModel: CurrentSemesterViewModel = viewModel()
        // Initialized the local storage DB
        val db = remember {
          Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mtucourses-db"
          ).addTypeConverter(MTUCoursesConverter()).build()
        }
        LaunchedEffect(Unit) {
          Log.d(
            "DEBUG",
            "Ran Initial Course List data grab"
          )
          semesterViewModel.initialCourselist(
            db,
            context
          )
        }
        MainView(db)
      }
    }
  }
}


