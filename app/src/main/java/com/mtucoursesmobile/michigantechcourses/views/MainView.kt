package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase

@Composable
fun MainView(db: AppDatabase) {
  var selectedItem by remember {
    mutableIntStateOf(0)
  }
  val items = remember {
    listOf(
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
  }
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