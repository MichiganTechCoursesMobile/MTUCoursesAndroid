package com.mtucoursesmobile.michigantechcourses.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyCourseList(
  listState: LazyListState,
  courseViewModel: MTUCoursesViewModel,
  navController: NavController, innerPadding: PaddingValues
) {
  val context = LocalContext.current
  val courses = remember { courseViewModel.filteredCourseList }
  val refreshState = rememberPullToRefreshState()
  val scope = rememberCoroutineScope()
  val scaleFraction =
    if (refreshState.isRefreshing) 1f else LinearOutSlowInEasing.transform(refreshState.progress)
      .coerceIn(
        0f,
        1f
      )
  if (refreshState.isRefreshing) {
    LaunchedEffect(true) {
      courseViewModel.updateSemester(
        context,
        refreshState
      )
    }
  }
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
      .nestedScroll(refreshState.nestedScrollConnection)
  ) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      itemsIndexed(
        items = courses.filter { course ->
          course.value.deletedAt == null && (course.value.subject + course.value.crse + course.value.title).contains(
            courseViewModel.courseSearchValue.value,
            ignoreCase = true
          )
        }.toList().sortedBy { item -> "${item.second.subject}${item.second.crse}" }.ifEmpty {
          if (courseViewModel.courseList.isNotEmpty() && courseViewModel.sectionList.isNotEmpty()) {
            listOf(
              Pair(
                "404",
                MTUCourses(
                  "404",
                  404,
                  "404",
                  "404",
                  "404",
                  "404",
                  null,
                  "404",
                  null,
                  "404",
                  listOf("404"),
                  404.0,
                  404.0
                )
              )
            )
          } else {
            emptyList()
          }

        },
        key = { _, item -> item.first }
      )
      { _, item ->
        if (item.first == "404") {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(top = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No courses found with this filter",
            )
          }
        } else {
          CourseItem(
            item,
            navController
          )
        }
      }
    }
    PullToRefreshContainer(
      state = refreshState,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .graphicsLayer(
          scaleFraction,
          scaleFraction
        )
        .offset(y = 6.dp)
    )
  }
}