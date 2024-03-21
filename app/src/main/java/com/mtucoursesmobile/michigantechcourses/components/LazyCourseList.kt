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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.api.getSemesterCourses
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LazyCourseList(
  innerPadding: PaddingValues, currentSemester: CurrentSemester, db: AppDatabase,
  searchBarVal: String, listState: LazyListState
) {
  val context = LocalContext.current
  val courseList = remember {
    mutableStateListOf<MTUCourses>()
  }
  LaunchedEffect(currentSemester) {
    GlobalScope.launch(Dispatchers.Default) {
      getSemesterCourses(
        courseList,
        context,
        currentSemester.semester,
        currentSemester.year,
        db
      )
    }
  }
  LazyColumn(
    state = listState,
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(courseList.filter { course -> course.title.contains(searchBarVal) }) { _, item ->
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
