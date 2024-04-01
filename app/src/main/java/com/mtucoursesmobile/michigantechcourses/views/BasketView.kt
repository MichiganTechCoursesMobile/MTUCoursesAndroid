package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketView(courseViewModel: MTUCoursesViewModel) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        title = { Text(text = "Baskets for ${semesterText.value}") },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
          SemesterPicker(
            expanded,
            courseViewModel,
            context,
            semesterText
          )
        }
      )
    }
  ) { innerPadding ->
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Tab 1", "Tab 2", "Tab 3")
    Column(Modifier.padding(innerPadding)) {
      SecondaryScrollableTabRow(selectedTabIndex = state, indicator = @Composable {
        FancyIndicator(Modifier.tabIndicatorOffset(it[state]))
      }) {
        titles.forEachIndexed { index, title ->
          Tab(
            selected = state == index,
            onClick = { state = index },
            text = { Text(text = title) }
          )
        }
      }
      Text(text = "Tab ${state + 1}")
    }
  }
}

@Composable
fun FancyIndicator(modifier: Modifier = Modifier) {
  Box(
    modifier
      .padding(5.dp)
      .fillMaxSize()
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), RoundedCornerShape(5.dp))
  )
}