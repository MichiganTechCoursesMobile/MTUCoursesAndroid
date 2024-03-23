package com.mtucoursesmobile.michigantechcourses

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.components.CourseView
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel

class MainActivity : ComponentActivity() {

  @OptIn(ExperimentalMaterial3Api::class)
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
        // Initialized the local storage DB
        val db = Room.databaseBuilder(
          LocalContext.current,
          AppDatabase::class.java,
          "mtucourses-db"
        ).addTypeConverter(MTUCoursesConverter()).build()
        var selectedItem by remember {
          mutableIntStateOf(0)
        }
        val context = LocalContext.current
        val semesterViewModel: CurrentSemesterViewModel = viewModel()
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
        val items = listOf(
          Pair(
            "Courses",
            Icons.Outlined.School,
          ),
          Pair(
            "Basket",
            Icons.Outlined.ShoppingBasket,
          ),
          Pair(
            "Settings",
            Icons.Outlined.Settings,
          )
        )
        Scaffold(
          contentWindowInsets = WindowInsets(0.dp),
          bottomBar = {
            NavigationBar {
              items.forEachIndexed { index, item ->
                NavigationBarItem(
                  label = { Text(text = item.first) },
                  selected = selectedItem == index,
                  onClick = { selectedItem = index },
                  icon = {
                    Icon(
                      imageVector = item.second,
                      contentDescription = item.first
                    )
                  },
                  alwaysShowLabel = false
                )
              }
            }
          }) { innerPadding ->
          when (selectedItem) {
            0 -> CourseView(
              db,
              innerPadding
            )
          }

        }
      }
    }
  }
}


