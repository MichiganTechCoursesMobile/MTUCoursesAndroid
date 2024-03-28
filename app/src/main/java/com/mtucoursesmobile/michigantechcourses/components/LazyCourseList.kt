package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyCourseList(
  listState: LazyListState,
  courseViewModel: MTUCoursesViewModel,
  navController: NavController, innerPadding: PaddingValues
) {
  val context = LocalContext.current
  val courses = remember { courseViewModel.filteredCourseList }
  val scope = rememberCoroutineScope()
  val refreshState = rememberPullToRefreshState()
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
        items = if (courses.contains(
            MTUCoursesEntry(
              courseId = "404",
              entry = MTUCourseSectionBundle(
                mutableListOf(),
                mutableListOf()
              ),
              semester = "404",
              year = "404"
            )
          )
        ) {
          courses
        } else {
          courses.filter { course ->
            course.entry.course[0].deletedAt == null && (course.entry.course[0].subject + course.entry.course[0].crse + course.entry.course[0].title).contains(
              courseViewModel.courseSearchValue.value,
              ignoreCase = true
            )
          }
        },
        key = { _, item -> item.courseId }
      )
      { _, item ->
        if (item.courseId == "404" && item.semester == "404") {
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
      modifier = Modifier.align(Alignment.TopCenter)
    )
  }
}