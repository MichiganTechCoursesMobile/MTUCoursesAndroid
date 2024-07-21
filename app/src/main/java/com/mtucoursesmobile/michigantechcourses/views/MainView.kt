package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
  courseViewModel: MTUCoursesViewModel,
  basketViewModel: BasketViewModel,
  db: BasketDB
) {
  val items = remember {
    listOf(
      Pair(
        "Courses",
        Pair(
          Icons.Outlined.School,
          Icons.Filled.School
        )
      ),
      Pair(
        "Baskets",
        Pair(
          Icons.Outlined.ShoppingBasket,
          Icons.Filled.ShoppingBasket
        )
      ),
      Pair(
        "Calendar",
        Pair(
          Icons.Outlined.CalendarMonth,
          Icons.Filled.CalendarMonth
        )
      )
    )
  }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  val navController = rememberNavController()
  val courseNavController = rememberNavController()
  LaunchedEffect(Unit) {
    while (true) {
      if (!courseViewModel.courseNotFound.value && courseViewModel.courseList.isNotEmpty()) {
        courseViewModel.updateSemester(
          context,
          null
        )
      }
      delay(30000)
    }
  }
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets(0.dp),
    bottomBar = {
      NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { _, item ->
          NavigationBarItem(
            label = { Text(text = item.first) },
            selected = currentDestination?.hierarchy?.any { it.route == item.first } == true,
            onClick = {
              // Scroll Back to top (No need to run rest of code)
              if (navController.currentBackStackEntry?.destination?.route.toString() == "Courses" && item.first == "Courses") {
                if (courseNavController.currentDestination?.route != "courseList") {
                  courseNavController.navigate("courseList")
                } else {
                  scope.launch { listState.animateScrollToItem(0) }
                }
                return@NavigationBarItem
              }
              scope.launch {
                navController.navigate(item.first) {

                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  // Restore state when reelecting a previously selected item
                  restoreState = true

                }
              }
            },
            icon = {
              AnimatedContent(targetState = currentDestination?.hierarchy?.any { it.route == item.first } == true,
                label = item.first) { targetState ->
                if (targetState) {
                  Icon(
                    imageVector = item.second.second,
                    contentDescription = item.first
                  )
                } else {
                  Icon(
                    imageVector = item.second.first,
                    contentDescription = item.first
                  )
                }
              }
            },
            alwaysShowLabel = false
          )
        }
      }
    }) { innerPadding ->
    // Bottom Nav Bar
    NavHost(
      navController = navController,
      startDestination = "Courses",
      Modifier
        .padding(innerPadding)
        .fillMaxSize(),
      enterTransition = { EnterTransition.None },
      exitTransition = { ExitTransition.None }
    ) {
      composable(
        "Courses",
        enterTransition = {
          slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        exitTransition = {
          slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        }) {
        // Nested NavHost for Courses
        CourseNav(
          courseViewModel,
          basketViewModel,
          db,
          listState,
          courseNavController
        )
      }
      composable("Baskets",
        enterTransition = {
          if (this.initialState.destination.route.toString() == "Calendar") {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
          } else {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
          }
        },
        exitTransition = {
          if (this.targetState.destination.route.toString() == "Calendar") {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
          } else {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
          }
        }) {
        BasketView(
          courseViewModel,
          basketViewModel,
          db,
          listState,
          navController,
          courseNavController
        )
      }
      composable("Calendar",
        enterTransition = {
          slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
          slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }) {
        CalendarView(
          basketViewModel,
          courseViewModel,
          db
        )
      }
    }
  }
}

// Courses
@Composable
fun CourseNav(
  courseViewModel: MTUCoursesViewModel,
  basketViewModel: BasketViewModel,
  db: BasketDB,
  listState: LazyListState,
  courseNavController: NavHostController
) {
  NavHost(
    navController = courseNavController,
    startDestination = "courseList",
    modifier = Modifier.fillMaxSize(),
  ) {
    composable("courseList",
      enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
      },
      exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
      }) {
      CourseView(
        courseViewModel,
        basketViewModel,
        db,
        courseNavController,
        listState
      )
    }
    composable("courseDetail/{courseId}",
      arguments = listOf(navArgument("courseId") { type = NavType.StringType }),
      enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
      },
      exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
      }) { backStackEntry ->
      CourseDetailView(
        courseViewModel,
        basketViewModel,
        courseNavController,
        backStackEntry.arguments?.getString("courseId"),
        db
      )
    }
  }
}