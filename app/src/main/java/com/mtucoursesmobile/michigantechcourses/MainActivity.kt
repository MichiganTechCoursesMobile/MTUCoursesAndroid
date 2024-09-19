package com.mtucoursesmobile.michigantechcourses

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketConverters
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import com.mtucoursesmobile.michigantechcourses.views.MainView
import kotlinx.coroutines.launch


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
  name = "setting"
)

class MainActivity : ComponentActivity() {
  lateinit var userPreferences: UserPreferences
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
    userPreferences = UserPreferences(dataStore)
    setContent {
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
      var currentTheme by remember { mutableStateOf(false) }
      currentTheme = isSystemInDarkTheme()
      when (userPreferences.themeMode.toString()) {
        "default" -> currentTheme = isSystemInDarkTheme()
        "light" -> currentTheme = false
        "dark" -> currentTheme = true
      }
      MichiganTechCoursesTheme(currentTheme) {
        MainView(
          coursesViewModel,
          basketViewModel,
          db
        )
      }
    }
  }
}


