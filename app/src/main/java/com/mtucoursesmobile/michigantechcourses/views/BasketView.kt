package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class
)
@Composable
fun BasketView(courseViewModel: MTUCoursesViewModel) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val haptics = LocalHapticFeedback.current
  var state by remember { mutableIntStateOf(0) }
  val titles = remember {
    mutableStateListOf(
      "Basket 1"
    )
  }
  var showModal by remember { mutableStateOf(false) }
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
    Column(Modifier.padding(innerPadding)) {
      SecondaryScrollableTabRow(
        divider = { },
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = state,
        indicator = @Composable {
          FancyIndicator(Modifier.tabIndicatorOffset(it[state]))
        }) {
        titles.forEachIndexed { index, title ->
          Box(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .padding(10.dp)
              .clip(RoundedCornerShape(2.dp))
              .combinedClickable(onClick = { state = index },
                onLongClick = {
                  haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                  state = index
                  showModal = true
                }),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = title,
              textAlign = TextAlign.Center,
              modifier = Modifier
            )
          }

        }
        Box() {
          IconButton(
            onClick = {
              titles.add("Basket ${titles.size + 1}")
            }
          ) {
            Icon(
              modifier = Modifier,
              imageVector = Icons.Outlined.PostAdd,
              contentDescription = "Add new Tab"
            )
          }
        }

      }
      HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
      )
      Text(text = titles[state])
    }
  }
  if (showModal) {
    ModalBottomSheet(onDismissRequest = { showModal = false }) {
      val title = titles[state]
      Text(text = titles[state])
      if (titles.size > 1) {
        Button(onClick = {
          showModal = false
          titles.removeAt(state)
          if (state - 1 >= 0) {
            state -= 1
          }
        }) {
          Text(text = "Delete $title")
        }
      }
    }
  }
}

@Composable
fun FancyIndicator(modifier: Modifier = Modifier) {
  Box(
    modifier
      .padding(5.dp)
      .fillMaxSize()
      .border(
        BorderStroke(
          2.dp,
          MaterialTheme.colorScheme.primary
        ),
        RoundedCornerShape(5.dp)
      )
  )
}