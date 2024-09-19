package com.mtucoursesmobile.michigantechcourses.views

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mtucoursesmobile.michigantechcourses.R
import com.mtucoursesmobile.michigantechcourses.components.LoadingSpinnerAnimation
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.components.courses.ExpandableSearchView
import com.mtucoursesmobile.michigantechcourses.components.courses.FilterModal
import com.mtucoursesmobile.michigantechcourses.components.courses.LazyCourseList
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.launch


@OptIn(
  ExperimentalMaterial3Api::class,
)
@Composable
fun CourseView(
  courseViewModel: MTUCoursesViewModel,
  basketViewModel: BasketViewModel,
  db: BasketDB,
  navController: NavController,
  listState: LazyListState,
  viewSettings: DrawerState
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val expanded = remember { mutableStateOf(false) }
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val expandedFab by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
  }
  val (searching, onSearchExpandedChanged) = remember {
    mutableStateOf(false)
  }

  // Ensure that the search bar is open when re-loading page (coming back from course detail view)
  LaunchedEffect(Unit) {
    if (courseViewModel.courseSearchValue.value != "") {
      onSearchExpandedChanged(true)
    }
  }
  BackHandler(searching) {
    courseViewModel.courseSearchValue.value = ""
    onSearchExpandedChanged(false)
    scope.launch { listState.animateScrollToItem(0) }
  }

  Scaffold(
    modifier = Modifier,
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = if (expandedFab) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = if (expandedFab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
          AnimatedVisibility(visible = !searching) {
            AnimatedVisibility(
              visible = (courseViewModel.courseList.isNotEmpty() && courseViewModel.sectionList.isNotEmpty()),
              enter = scaleIn(
                animationSpec = tween(
                  delayMillis = 700
                )
              ),
              exit = scaleOut(
                animationSpec = tween(
                  delayMillis = 700
                )
              )
            ) {
              IconButton(onClick = { onSearchExpandedChanged(true) }) {
                Icon(
                  imageVector = Icons.Outlined.Search,
                  contentDescription = "Search Courses",
                  tint = if (expandedFab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
            }
          }
          AnimatedVisibility(visible = !searching) {
            SemesterPicker(
              expanded = expanded,
              courseViewModel = courseViewModel,
              basketViewModel = basketViewModel,
              db = db,
              context = context,
              semesterText = semesterText,
              expandedFab = expandedFab
            )
          }
          AnimatedVisibility(visible = !searching) {
            IconButton(onClick = {
              scope.launch {
                viewSettings.open()
              }
            }) {
              Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Open Settings",
                tint = if (expandedFab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
              )
            }
          }
        },
        title = {
          if (!searching) {
            Text(text = "Courses for ${semesterText.value}")
          }
          ExpandableSearchView(
            searchDisplay = courseViewModel.courseSearchValue.value,
            onSearchDisplayChanged = { courseViewModel.courseSearchValue.value = it },
            onSearchDisplayClosed = {
              onSearchExpandedChanged(false)
              courseViewModel.courseSearchValue.value = ""
              scope.launch { listState.animateScrollToItem(0) }
            },
            expanded = searching,
            onExpandedChanged = onSearchExpandedChanged
          )
        }
      )
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = (courseViewModel.courseList.isNotEmpty() && courseViewModel.sectionList.isNotEmpty()),
        enter = scaleIn(
          animationSpec = tween(
            delayMillis = 700
          )
        ),
        exit = scaleOut(
          animationSpec = tween(
            delayMillis = 700
          )
        ),
      ) {
        ExtendedFloatingActionButton(
          onClick = {
            courseViewModel.showFilter.value = true
          },
          expanded = expandedFab,
          icon = {
            Icon(
              Icons.Filled.FilterList,
              "Filter Button"
            )
          },
          text = { Text(text = "Filter") },
        )
      }
    }) { innerPadding ->
    if (courseViewModel.courseNotFound.value) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "404 Courses not found",
        )
        AsyncImage(
          model = R.drawable.cat404,
          contentDescription = "404 Cat"
        )
      }
    } else {
      AnimatedContent(
        targetState = (courseViewModel.courseList.isEmpty() && courseViewModel.sectionList.isEmpty()),
        label = "CourseList",
      ) { isEmpty ->
        if (isEmpty) {
          LoadingSpinnerAnimation(innerPadding)
        } else {
          LazyCourseList(
            listState = listState,
            courseViewModel,
            navController,
            innerPadding
          )
        }
      }
    }
    FilterModal(
      listState = listState,
      courseViewModel
    )
  }
}