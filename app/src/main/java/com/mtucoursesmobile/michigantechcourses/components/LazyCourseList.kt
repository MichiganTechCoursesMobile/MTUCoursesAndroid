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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
      items = semesterViewModel.courseList.filter { course ->
        (course.subject.uppercase() in (courseFilterViewModel.courseTypes.filter { it -> it.second.value }
          .map { it.first }.ifEmpty { courseFilterViewModel.courseTypes.map { it.first } }).filter {
            (course.crse.first()
              .toString() in (courseFilterViewModel.courseLevels.filter { it -> it.second.value }
              .map { it.first.first().toString() }
              .ifEmpty {
                courseFilterViewModel.courseLevels.map {
                  it.first.first().toString()
                }
              }).filter {
                (course.maxCredits.toString().first()
                  .toString() in (courseFilterViewModel.courseCredits.filter { it -> it.second.value }
                  .map { it.first.first().toString() }
                  .ifEmpty {
                    (1..100).map { it.toString() }
                  }))
              })
          }.filter { course.title.contains(courseFilterViewModel.searchBarValue.value) })
      },
      key = { _, item -> item.id }
    ) { _, item ->
      Log.d(
        "DEBUG1",
        item.minCredits.toString()
      )
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
