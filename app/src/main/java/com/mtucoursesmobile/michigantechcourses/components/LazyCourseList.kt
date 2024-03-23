package com.mtucoursesmobile.michigantechcourses.components

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import kotlin.random.Random

@Composable
fun LazyCourseList(
  innerPadding: PaddingValues, listState: LazyListState
) {
  val semesterViewModel: CurrentSemesterViewModel = viewModel()
  val courseFilterViewModel: CourseFilterViewModel = viewModel()

  LazyColumn(
    state = listState,
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(
      items =
      if (courseFilterViewModel.typeFilter.isEmpty() && courseFilterViewModel.levelFilter.isEmpty() && courseFilterViewModel.creditFilter.isEmpty() && courseFilterViewModel.otherFilter.isEmpty()) {
        semesterViewModel.courseList.filter { course ->
          course.title.contains(courseFilterViewModel.searchBarValue.value)
        }
      } else {
        val courses = semesterViewModel.courseList.toMutableList()
        if (courseFilterViewModel.typeFilter.isNotEmpty()) {
          for (i in semesterViewModel.courseList.filter { course -> course.subject !in courseFilterViewModel.typeFilter }) {
            courses.remove(i)
          }
        }
        if (courseFilterViewModel.levelFilter.isNotEmpty()) {
          for (i in semesterViewModel.courseList.filter { course ->
            course.crse.first().toString() !in courseFilterViewModel.levelFilter
          }) {
            courses.remove(i)
          }
        }
        if (courseFilterViewModel.creditFilter.isNotEmpty()) {
          for (i in semesterViewModel.courseList.filter { course ->
            course.maxCredits.toString() !in courseFilterViewModel.creditFilter
          }) {
            courses.remove(i)
          }
        }
        courses.filter { course ->
          course.title.contains(courseFilterViewModel.searchBarValue.value)
        }
      },
      key = { _, item -> item.id }
    ) { _, item ->
      ElevatedCard(
        elevation = CardDefaults.cardElevation(
          defaultElevation = 4.dp
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(10.dp),
      ) {
        Text(
          text = "${item.subject}${item.crse} - ${item.title}",
          modifier = Modifier
            .padding(
              horizontal = 10.dp
            )
            .paddingFromBaseline(
              top = 30.dp,
              bottom = 10.dp
            ),
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          textAlign = TextAlign.Left,
        )
        Text(
          text = if (item.description != null) item.description else "¯\\_(ツ)_/¯",
          modifier = Modifier.padding(horizontal = 10.dp),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

fun List<Any>?.filterQueryText(queryText: String?) = this?.filter {
  queryText.equals(it.toString())
}?.run { ifEmpty { this@filterQueryText } }.orEmpty()