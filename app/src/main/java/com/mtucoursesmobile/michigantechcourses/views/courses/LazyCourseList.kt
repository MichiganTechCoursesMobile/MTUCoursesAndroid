package com.mtucoursesmobile.michigantechcourses.views.courses

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
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.components.courses.CourseItem
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyCourseList(
  listState: LazyListState,
  courseViewModel: CourseViewModel,
  navController: NavController,
  innerPadding: PaddingValues
) {
  val context = LocalContext.current
  val isRefreshing = remember { mutableStateOf(false) }
  val refreshState = rememberPullToRefreshState()
  val scope = rememberCoroutineScope()
  val onRefresh: () -> Unit = {
    isRefreshing.value = true
    scope.launch {
      courseViewModel.updateSemester(
        context,
        isRefreshing
      )
    }
  }
  val scaleFactor = {
    if (isRefreshing.value) 1f
    else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(
      0f,
      1f
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(innerPadding),
  ) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .pullToRefresh(
          isRefreshing = isRefreshing.value,
          state = refreshState,
          onRefresh = onRefresh
        ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      itemsIndexed(
        items = courseViewModel.filteredCourseList.filter { course ->
          course.value.deletedAt == null && (course.value.subject + course.value.crse + course.value.title).contains(
            courseViewModel.courseSearchValue.value,
            ignoreCase = true
          )
        }.toList().sortedBy { item ->
          if (courseViewModel.sortingMode.value.first == "Credits" && courseViewModel.sortingMode.value.second == "ascending") {
            item.second.maxCredits.toInt()
          } else {
            0
          }
        }.sortedBy { item ->
          if (courseViewModel.sortingMode.value.first == "Subject & Level" && courseViewModel.sortingMode.value.second == "ascending") {
            "${item.second.subject}${item.second.crse}"
          } else {
            ""
          }
        }.sortedByDescending { item ->
          if (courseViewModel.sortingMode.value.first == "Credits" && courseViewModel.sortingMode.value.second == "descending") {
            item.second.maxCredits.toInt()
          } else {
            0
          }
        }.sortedByDescending { item ->
          if (courseViewModel.sortingMode.value.first == "Subject & Level" && courseViewModel.sortingMode.value.second == "descending") {
            "${item.second.subject}${item.second.crse}"
          } else {
            ""
          }
        }.ifEmpty {
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
    Box(
      Modifier
        .align(Alignment.TopCenter)
        .graphicsLayer {
          scaleX = scaleFactor()
          scaleY = scaleFactor()
        }
        .offset(y = 6.dp)
    ) {
      PullToRefreshDefaults.Indicator(
        state = refreshState,
        isRefreshing = isRefreshing.value
      )
    }
  }
}