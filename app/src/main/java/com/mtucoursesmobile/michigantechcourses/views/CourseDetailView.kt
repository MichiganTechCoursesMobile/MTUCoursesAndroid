package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailView(
  semesterViewModel: CurrentSemesterViewModel,
  courseFilterViewModel: CourseFilterViewModel,
  courseId: String?
) {
  val foundCourse =
    semesterViewModel.courseList.find { course -> course.courseId == courseId }?.entry
  if (foundCourse != null) {
    Scaffold(
      contentWindowInsets = WindowInsets(0.dp),
      topBar = {
        TopAppBar(
          title = { Text(text = foundCourse.course[0].title) },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
          )
        )
      }) { innerPadding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        itemsIndexed(
          items = foundCourse.sections.filter { section -> section?.deletedAt == null },
          key = { _, item -> item!!.id }) { _, item ->
          ElevatedCard(
            elevation = CardDefaults.cardElevation(
              defaultElevation = 4.dp
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .padding(10.dp),
          ) {
            Text(item?.section.toString())
          }
        }
      }
    }
  }
}