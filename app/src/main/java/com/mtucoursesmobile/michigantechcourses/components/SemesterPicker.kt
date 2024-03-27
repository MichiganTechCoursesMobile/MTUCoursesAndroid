package com.mtucoursesmobile.michigantechcourses.components

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Year

@Composable
fun SemesterPicker(
  expanded: MutableState<Boolean>,
  listState: LazyListState,
  semesterViewModel: CurrentSemesterViewModel,
  scope: CoroutineScope,
  context: Context,
  semesterText: MutableState<String>
) {


  val scope = rememberCoroutineScope()
  val semesterTypes = listOf(
    "Fall",
    "Summer",
    "Spring"
  )
  val currentYears = mutableListOf<Number>()
  for (year in -1..1) {
    currentYears.add(Year.now().value + year)
  }

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
    for (i in semesterTypes) {
      DropdownMenuItem(
        text = { Text(i) },
        onClick = {
          if (semesterViewModel.currentSemester.semester.lowercase() != i.lowercase()) {
            expanded.value = false
            scope.launch {
              semesterViewModel.updateSemesterPeriod(
                i,
                context
              )
              semesterText.value = "$i ${semesterViewModel.currentSemester.year}"
            }
          }
        },
        trailingIcon = {
          if (semesterViewModel.currentSemester.semester.lowercase() == i.lowercase())
            Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = "Check",
              tint = MaterialTheme.colorScheme.primary
            )
        }
      )
    }
    HorizontalDivider()
    for (i in currentYears) {
      DropdownMenuItem(
        text = { Text(i.toString()) },
        onClick = {
          if (semesterViewModel.currentSemester.year != i.toString()) {
            expanded.value = false
            scope.launch {
              semesterViewModel.updateSemesterYear(
                i,
                context
              )
              semesterText.value = "${
                semesterViewModel.currentSemester.semester.lowercase()
                  .replaceFirstChar(Char::titlecase)
              } $i"
            }
          }
        },
        trailingIcon = {
          if (semesterViewModel.currentSemester.year == i.toString())
            Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = "Check",
              tint = MaterialTheme.colorScheme.primary
            )
        }
      )
    }
  }
}