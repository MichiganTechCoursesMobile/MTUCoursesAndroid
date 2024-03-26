package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel

@Composable
fun MainView() {
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

  val scope = rememberCoroutineScope()
  val semesterViewModel: CurrentSemesterViewModel = viewModel()
  val courseFilterViewModel: CourseFilterViewModel = viewModel()
  val selectedItem = remember { mutableIntStateOf(0) }
  val navController = rememberNavController()
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    bottomBar = {
      NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, item ->
          NavigationBarItem(
            label = { Text(text = item.first) },
            selected = selectedItem.intValue == index,
            onClick = {
              selectedItem.intValue = index
            },
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
    NavHost(
      navController = navController,
      startDestination = "Courses",
      Modifier
        .padding(innerPadding)
        .fillMaxSize()
    ) {
      composable(
        "Courses",
        enterTransition = {
          slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        exitTransition = {
          slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        }) {
        CourseNav(semesterViewModel, courseFilterViewModel)
      }
      composable("Basket",
        enterTransition = {
          if (this.initialState.destination.route.toString() == "Settings") {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
          } else {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
          }
        },
        exitTransition = {
          if (this.targetState.destination.route.toString() == "Settings") {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
          } else {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
          }
        }) {
        BasketView()
      }
      composable("Settings",
        enterTransition = {
          slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
          slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }) {
        SettingsView()
      }
    }
  }
}

// Courses
@Composable
fun CourseNav(semesterViewModel: CurrentSemesterViewModel, courseFilterViewModel: CourseFilterViewModel) {
  val courseNavController = rememberNavController()
  NavHost(
    navController = courseNavController,
    startDestination = "courseList",
    modifier = Modifier,
  ) {
    composable("courseList",
      enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
      },
      exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
      }) {
      CourseView(
        semesterViewModel = semesterViewModel,
        courseFilterViewModel = courseFilterViewModel,
        courseNavController
      )
    }
    composable("courseDetail/{courseId}",
      arguments = listOf(navArgument("courseId") {type = NavType.StringType}),
      enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
      },
      exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
      }) { backStackEntry ->
      CourseDetailView(semesterViewModel, courseFilterViewModel, backStackEntry.arguments?.getString("courseId"))
    }
  }
}