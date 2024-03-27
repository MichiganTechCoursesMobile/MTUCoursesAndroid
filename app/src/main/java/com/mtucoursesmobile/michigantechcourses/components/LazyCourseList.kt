package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyCourseList(
  listState: LazyListState,
  courseFilterViewModel: CourseFilterViewModel, semesterViewModel: CurrentSemesterViewModel,
  navController: NavController, innerPadding: PaddingValues
) {
  val context = LocalContext.current
  val courses = remember { mutableStateOf(semesterViewModel.courseList.toMutableList()) }
  val refreshState = rememberPullToRefreshState()
  if (refreshState.isRefreshing) {
    LaunchedEffect(true) {
      semesterViewModel.updateSemester(
        semesterViewModel.currentSemester,
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
        items =
        if (courseFilterViewModel.typeFilter.isEmpty() && courseFilterViewModel.creditFilter.value == 1f..4f && courseFilterViewModel.creditFilter.value == 0f..4f && courseFilterViewModel.otherFilter.isEmpty()) {
          semesterViewModel.courseList.filter { course ->
            course.entry.course[0].deletedAt == null && (course.entry.course[0].subject + course.entry.course[0].crse + course.entry.course[0].title).contains(
              courseFilterViewModel.searchBarValue.value,
              ignoreCase = true
            )
          }
        } else {
          courses.value.clear()
          courses.value = semesterViewModel.courseList.toMutableList()

          //Type
          if (courseFilterViewModel.typeFilter.isNotEmpty()) {
            courses.value.removeAll(semesterViewModel.courseList.filter { course -> course.entry.course[0].subject !in courseFilterViewModel.typeFilter })
          }

          //Level
          if (courseFilterViewModel.levelFilter.value != 1f..4f) {
            when (courseFilterViewModel.levelFilter.value) {
              1f..1f -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course ->
                  (!(course.entry.course[0].crse.first().toString().toFloat() <= 1.0))
                })
              }

              4f..4f -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course ->
                  (!(course.entry.course[0].crse.first().toString().toFloat() >= 4.0))
                })
              }

              else -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course ->
                  !courseFilterViewModel.levelFilter.value.contains(
                    course.entry.course[0].crse.first().toString().toFloat()
                  )
                })
              }
            }
          }
          //Credit
          if (courseFilterViewModel.creditFilter.value != 0f..4f) {
            when (courseFilterViewModel.creditFilter.value) {
              0f..0f -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course -> (!(course.entry.course[0].maxCredits <= 1.0)) })
              }

              4f..4f -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course -> (!(course.entry.course[0].maxCredits >= 4.0)) })
              }

              else -> {
                courses.value.removeAll(semesterViewModel.courseList.filter { course ->
                  (!courseFilterViewModel.creditFilter.value.contains(course.entry.course[0].maxCredits) || !courseFilterViewModel.creditFilter.value.contains(course.entry.course[0].minCredits))
                })
              }
            }
          }
          //Other
          if (courseFilterViewModel.otherFilter.isNotEmpty()) {
            for (other in courseFilterViewModel.otherFilter) {
              when (other) {
                "Has Seats" -> {
                  courses.value.removeAll(semesterViewModel.courseList.filter { course ->
                    course.entry.sections.none { section -> section?.availableSeats?.toFloat()!! > 0 }
                  })
                }
              }
            }
          }
          courses.value.filter { course ->
            course.entry.course[0].deletedAt == null && (course.entry.course[0].subject + course.entry.course[0].crse + course.entry.course[0].title).contains(
              courseFilterViewModel.searchBarValue.value,
              ignoreCase = true
            )
          }
        },
        key = { _, item -> item.entry.course[0].id }
      )
      { _, item ->
        CourseItem(
          item,
          navController
        )
      }
    }
    PullToRefreshContainer(
      state = refreshState,
      modifier = Modifier.align(Alignment.TopCenter)
    )
  }
}