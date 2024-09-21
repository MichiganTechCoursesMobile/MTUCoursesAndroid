package com.mtucoursesmobile.michigantechcourses.components.courses

import android.content.Context
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
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSemesters
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import kotlinx.coroutines.launch
import java.time.Year

@Composable
fun SemesterPicker(
  expanded: MutableState<Boolean>,
  currentSemester: CurrentSemester,
  semesterList: List<MTUSemesters>,
  updateSemesterPeriod: (String, Context) -> Unit,
  updateSemesterYear: (Number, Context) -> Unit,
  getSemesterBaskets: (CurrentSemester, BasketDB) -> Unit,
  db: BasketDB,
  context: Context,
  semesterText: MutableState<String>,
  courseNavController: NavController? = null,
  expandedFab: Boolean? = true
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
      tint = if (expandedFab!!) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }
  DropdownMenu(
    expanded = expanded.value,
    onDismissRequest = { expanded.value = false }) {
    for (i in semesterTypes) {
      DropdownMenuItem(
        text = { Text(i) },
        onClick = {
          if (currentSemester.semester.lowercase() != i.lowercase()) {
            expanded.value = false
            scope.launch {
              courseNavController?.navigate("courseList")
              updateSemesterPeriod(
                i,
                context
              )
              getSemesterBaskets(
                CurrentSemester(
                  readable = "$i ${currentSemester.year}",
                  semester = i,
                  year = currentSemester.year
                ),
                db
              )
              semesterText.value = "$i ${currentSemester.year}"
            }
          }
        },
        trailingIcon = {
          if (currentSemester.semester.lowercase() == i.lowercase()) Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Check",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        enabled = semesterList.any { it.semester == i.uppercase() && it.year.toInt() == currentSemester.year.toInt() }
      )
    }
    HorizontalDivider()
    for (i in currentYears) {
      DropdownMenuItem(
        text = { Text(i.toString()) },
        onClick = {
          if (currentSemester.year != i.toString()) {
            expanded.value = false
            scope.launch {
              courseNavController?.navigate("courseList")
              updateSemesterYear(
                i,
                context
              )
              getSemesterBaskets(
                CurrentSemester(
                  readable = "${
                    currentSemester.semester.lowercase().replaceFirstChar(Char::titlecase)
                  } $i",
                  semester = currentSemester.semester,
                  year = i.toString()
                ),
                db
              )
              semesterText.value = "${
                currentSemester.semester.lowercase()
                  .replaceFirstChar(Char::titlecase)
              } $i"
            }
          }
        },
        trailingIcon = {
          if (currentSemester.year == i.toString()) Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Check",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        enabled = semesterList.any { it.semester == currentSemester.semester.uppercase() && it.year.toInt() == i.toInt() }
      )
    }
  }
}