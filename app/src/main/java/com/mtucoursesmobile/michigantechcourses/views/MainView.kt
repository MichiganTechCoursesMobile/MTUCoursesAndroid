package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.navigation.navDeepLink
import com.mtucoursesmobile.michigantechcourses.components.settings.SettingsModal
import com.mtucoursesmobile.michigantechcourses.utils.ReverseLayoutDirection
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import com.mtucoursesmobile.michigantechcourses.views.basket.BasketView
import com.mtucoursesmobile.michigantechcourses.views.calendar.CalendarView
import com.mtucoursesmobile.michigantechcourses.views.courses.CourseDetailView
import com.mtucoursesmobile.michigantechcourses.views.courses.CourseView
import kotlinx.coroutines.launch

@Composable
fun MainView(
  courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel
) {
  val viewSettings = rememberDrawerState(DrawerValue.Closed)
  // Navigation bar items
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
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  // Navigation controller for bottom nav bar
  val navController = rememberNavController()
  // Navigation controller for courses and detailed course views
  val courseNavController = rememberNavController()

  ReverseLayoutDirection {
    ModalNavigationDrawer(
      drawerContent = {
        ReverseLayoutDirection {
          ModalDrawerSheet(
            drawerState = viewSettings,
            drawerShape = RoundedCornerShape(16.dp),
            windowInsets = WindowInsets(0.dp)
          ) {
            val maxWidth = LocalConfiguration.current.smallestScreenWidthDp * 0.8f
            Box(modifier = Modifier.width(maxWidth.dp)) {
              SettingsModal(viewSettings)
            }
          }
        }
      },
      drawerState = viewSettings,
      gesturesEnabled = viewSettings.isOpen
    ) {
      ReverseLayoutDirection {
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
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                  ),
                  onClick = {
                    // Prevent unneeded navigation
                    if (navController.currentBackStackEntry?.destination?.route.toString() == item.first) {
                      // Handle logic for clicking Courses twice to scroll to the top of the page
                      if (item.first == "Courses") {
                        if (courseNavController.currentDestination?.route != "courseList") {
                          courseNavController.navigate("courseList")
                        } else {
                          scope.launch { listState.animateScrollToItem(0) } // Scrolls course list back to the top
                        }
                      }
                      return@NavigationBarItem
                    }
                    scope.launch {
                      navController.navigate(item.first) { // Navigate to the selected item

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
              deepLinks = listOf(navDeepLink { uriPattern = "https://mymtu.link/" }),
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
                listState,
                courseNavController,
                viewSettings
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
                courseViewModel,
                basketViewModel,
              )
            }
          }
        }
      }
    }
  }

}

// Navigation Handler for Courses and their Detailed Views/Pages
@Composable
fun CourseNav(
  courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel,
  listState: LazyListState,
  courseNavController: NavHostController,
  viewSettings: DrawerState
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
        courseNavController,
        listState,
        viewSettings
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
        backStackEntry.arguments?.getString("courseId")
      )
    }
  }
}

