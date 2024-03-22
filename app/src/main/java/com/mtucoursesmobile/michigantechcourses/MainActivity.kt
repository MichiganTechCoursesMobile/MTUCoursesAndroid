package com.mtucoursesmobile.michigantechcourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.components.CourseView
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import com.mtucoursesmobile.michigantechcourses.viewModels.currentSemesterViewModel

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
        var selectedItem by remember {
          mutableIntStateOf(0)
        }
        val items = listOf(
          Pair(
            "Courses",
            Icons.Filled.List,
          ),
          Pair(
            "Basket",
            Icons.Filled.ShoppingCart,
          ),
          Pair(
            "Settings",
            Icons.Filled.Settings,
          )
        )
        Scaffold(bottomBar = {
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


