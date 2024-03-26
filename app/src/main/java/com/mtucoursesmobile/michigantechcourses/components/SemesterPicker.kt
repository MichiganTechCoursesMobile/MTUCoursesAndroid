package com.mtucoursesmobile.michigantechcourses.components

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.semesterList
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SemesterPicker(
  expanded: MutableState<Boolean>, listState: LazyListState,
  semesterViewModel: CurrentSemesterViewModel, scope: CoroutineScope, context: Context,
  semesterText: MutableState<String>
) {
  IconButton(onClick = { expanded.value = true }) {
    Icon(
      imageVector = Icons.Outlined.DateRange,
      contentDescription = "Change Semester",
      tint = MaterialTheme.colorScheme.primary,
    )
  }
  DropdownMenu(
    expanded = expanded.value,
    onDismissRequest = { expanded.value = false }) {
    for (i in semesterList) {
      DropdownMenuItem(
        text = { Text(i.readable) },
        onClick = {
          if (i.readable != semesterViewModel.currentSemester.readable) {
            scope.launch {
              listState.animateScrollToItem(0)
              delay(100)
              semesterViewModel.courseList.clear()
              semesterViewModel.setSemester(
                i,
                context
              )
              semesterText.value = i.readable
            }
          } else {
            scope.launch {
              listState.animateScrollToItem(0)
            }
          }
          expanded.value = false
        })
    }
  }
}