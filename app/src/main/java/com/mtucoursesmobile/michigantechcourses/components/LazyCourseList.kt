package com.mtucoursesmobile.michigantechcourses.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.SearchBarViewModel

@Composable
fun LazyCourseList(
  innerPadding: PaddingValues, listState: LazyListState
) {
  val semesterViewModel: CurrentSemesterViewModel = viewModel()
  val searchBarViewModel: SearchBarViewModel = viewModel()
  LazyColumn(
    state = listState,
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(
      items = semesterViewModel.courseList.filter { course -> course.title.contains(searchBarViewModel.searchBarValue.value) },
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
          textAlign = TextAlign.Center,
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
