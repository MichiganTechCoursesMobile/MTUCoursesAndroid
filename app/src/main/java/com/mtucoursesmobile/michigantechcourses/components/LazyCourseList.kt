package com.mtucoursesmobile.michigantechcourses.components

import android.util.Log
import android.widget.Spinner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
  val courses = remember { mutableStateOf(semesterViewModel.courseList.toMutableList()) }

  LazyColumn(
    state = listState,
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(
      items =
      if (courseFilterViewModel.typeFilter.isEmpty() && courseFilterViewModel.creditFilter.value == 1f..4f && courseFilterViewModel.creditFilter.value == 0f..4f && courseFilterViewModel.otherFilter.isEmpty()) {
        courses.value.filter { course ->
          course.entry.course.title.contains(courseFilterViewModel.searchBarValue.value)
        }
      } else {
        courses.value.clear()
        courses.value = semesterViewModel.courseList.toMutableList()

        //Type
        if (courseFilterViewModel.typeFilter.isNotEmpty()) {
          for (i in semesterViewModel.courseList.filter { course -> course.entry.course.subject !in courseFilterViewModel.typeFilter }) {
            courses.value.remove(i)
          }
        }

        //Level
        if (courseFilterViewModel.levelFilter.value != 1f..4f) {
          when (courseFilterViewModel.levelFilter.value) {
            1f..1f -> {
              for (i in semesterViewModel.courseList.filter { course ->
                (!(course.entry.course.crse.first().toString().toFloat() <= 1.0))
              }) {
                courses.value.remove(i)
              }
            }

            4f..4f -> {
              for (i in semesterViewModel.courseList.filter { course ->
                (!(course.entry.course.crse.first().toString().toFloat() >= 4.0))
              }) {
                courses.value.remove(i)
              }
            }

            else -> {
              for (i in semesterViewModel.courseList.filter { course ->
                !courseFilterViewModel.levelFilter.value.contains(
                  course.entry.course.crse.first().toString().toFloat()
                )
              }) {
                courses.value.remove(i)
              }
            }
          }
        }
        //Credit
        if (courseFilterViewModel.creditFilter.value != 0f..4f) {
          when (courseFilterViewModel.creditFilter.value) {
            0f..0f -> {
              for (i in semesterViewModel.courseList.filter { course -> (!(course.entry.course.maxCredits <= 1.0)) }) {
                courses.value.remove(i)
              }
            }

            4f..4f -> {
              for (i in semesterViewModel.courseList.filter { course -> (!(course.entry.course.maxCredits >= 4.0)) }) {
                courses.value.remove(i)
              }
            }

            else -> {
              for (i in semesterViewModel.courseList.filter { course ->
                (!courseFilterViewModel.creditFilter.value.contains(course.entry.course.maxCredits) || !courseFilterViewModel.creditFilter.value.contains(course.entry.course.minCredits))
              }) {
                courses.value.remove(i)
              }
            }
          }
        }
        courses.value.filter { course ->
          course.entry.course.title.contains(courseFilterViewModel.searchBarValue.value)
        }
      },
      key = { _, item -> item.entry.course.id }
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
          text = "${item.entry.course.subject}${item.entry.course.crse} - ${item.entry.course.title} - (${item.entry.sections.size} section${if (item.entry.sections.size != 1) "s" else ""})",
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
          text = if (item.entry.course.description != null) item.entry.course.description else "¯\\_(ツ)_/¯",
          modifier = Modifier.padding(horizontal = 10.dp),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}