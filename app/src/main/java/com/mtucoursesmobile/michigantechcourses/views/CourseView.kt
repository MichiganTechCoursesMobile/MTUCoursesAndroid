package com.mtucoursesmobile.michigantechcourses.views

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mtucoursesmobile.michigantechcourses.classes.semesterList
import com.mtucoursesmobile.michigantechcourses.components.ExpandableSearchView
import com.mtucoursesmobile.michigantechcourses.components.FilterModal
import com.mtucoursesmobile.michigantechcourses.components.LazyCourseList
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterial3Api::class,
)
@Composable
fun CourseView(
  semesterViewModel: CurrentSemesterViewModel,
  courseFilterViewModel: CourseFilterViewModel, navController: NavController
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()
  var expanded by remember { mutableStateOf(false) }
  val expandedFab by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
  }
  val (searching, onSearchExpandedChanged) = remember {
    mutableStateOf(false)
  }

  LaunchedEffect(Unit) {
    if (courseFilterViewModel.searchBarValue.value != "") {
      onSearchExpandedChanged(true)
    }
  }

  BackHandler {
    if (searching) {
      onSearchExpandedChanged(false)
      scope.launch { listState.animateScrollToItem(0) }
    } else {
      (context as? Activity)?.finish()
    }
  }
  val semesterText = remember { mutableStateOf(semesterViewModel.currentSemester.readable) }
  Scaffold(modifier = Modifier,
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (expandedFab) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary
      ),
        actions = {
          if (!searching) {
            IconButton(onClick = { onSearchExpandedChanged(true) }) {
              Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search Courses",
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }
          IconButton(onClick = { expanded = true }) {
            Icon(
              imageVector = Icons.Outlined.DateRange,
              contentDescription = "Change Semester",
              tint = MaterialTheme.colorScheme.primary,
            )
          }
          DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            for (i in semesterList) {
              DropdownMenuItem(
                text = { Text(i.readable) },
                onClick = {
                  if (i.readable != semesterViewModel.currentSemester.readable) {
                    scope.launch {
                      listState.animateScrollToItem(0)
                      delay(100)
                      semesterViewModel.courseList.clear()
                      semesterViewModel.setSemester(
                        i,
                        context
                      )
                      semesterText.value = i.readable
                    }
                  } else {
                    scope.launch {
                      listState.animateScrollToItem(0)
                    }
                  }
                  expanded = false
                })
            }
          }
        },
        title = {
          if (!searching) {
            Text(text = "Courses for ${semesterText.value}")
          }
          ExpandableSearchView(
            searchDisplay = courseFilterViewModel.searchBarValue.value,
            onSearchDisplayChanged = { courseFilterViewModel.searchBarValue.value = it },
            onSearchDisplayClosed = {
              onSearchExpandedChanged(false)
              courseFilterViewModel.searchBarValue.value = ""
              scope.launch { listState.animateScrollToItem(0) }
            },
            expanded = searching,
            onExpandedChanged = onSearchExpandedChanged
          )
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          courseFilterViewModel.showFilter.value = true
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

    }) { innerPadding ->
    if (semesterViewModel.courseList.toMutableList().isEmpty()) {
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        CircularProgressIndicator(
          modifier = Modifier
            .width(64.dp),
          color = MaterialTheme.colorScheme.secondary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
      }
    } else {
      LazyCourseList(
        listState = listState,
        courseFilterViewModel,
        semesterViewModel,
        navController,
        innerPadding
      )
    }

    FilterModal(
      listState = listState,
      courseFilterViewModel
    )
  }
}