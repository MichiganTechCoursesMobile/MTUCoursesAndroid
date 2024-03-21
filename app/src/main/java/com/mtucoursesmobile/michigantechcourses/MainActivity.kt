package com.mtucoursesmobile.michigantechcourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.components.CourseView
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme

class MainActivity : ComponentActivity() {

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
        // Initialized the local storage DB
        val db = Room.databaseBuilder(
          LocalContext.current,
          AppDatabase::class.java,
          "mtucourses-db"
        ).addTypeConverter(MTUCoursesConverter()).build()
        Scaffold(bottomBar = {
          BottomAppBar {

          }
        }) { innerPadding ->
          CourseView(
            db,
            innerPadding
          )
        }
      }
    }
  }
}







