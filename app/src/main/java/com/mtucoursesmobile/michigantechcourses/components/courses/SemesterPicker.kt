package com.mtucoursesmobile.michigantechcourses.components.courses

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
import kotlinx.coroutines.launch
import java.time.Year
import kotlin.reflect.KFunction1

@Composable
fun SemesterPicker(
  expanded: MutableState<Boolean>,
  currentSemester: CurrentSemester,
  semesterList: List<MTUSemesters>,
  updateSemesterPeriod: KFunction1<String, Unit>,
  updateSemesterYear: KFunction1<Number, Unit>,
  getSemesterBaskets: (CurrentSemester) -> Unit,
  courseNavController: NavController? = null,
  topBarColor: Boolean? = true
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
      tint = if (topBarColor!!) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
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
                i
              )
              getSemesterBaskets(
                CurrentSemester(
                  readable = "$i ${currentSemester.year}",
                  semester = i,
                  year = currentSemester.year
                )
              )
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
                i
              )
              getSemesterBaskets(
                CurrentSemester(
                  readable = "${
                    currentSemester.semester.lowercase().replaceFirstChar(Char::titlecase)
                  } $i",
                  semester = currentSemester.semester,
                  year = i.toString()
                )
              )
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