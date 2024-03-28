package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.SectionInstructors
import com.mtucoursesmobile.michigantechcourses.components.SectionItem
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import java.text.DecimalFormat

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalLayoutApi::class
)
@Composable
fun CourseDetailView(
  semesterViewModel: CurrentSemesterViewModel,
  courseFilterViewModel: CourseFilterViewModel,
  navController: NavController,
  courseId: String?
) {
  val foundCourse =
    semesterViewModel.courseList.find { course -> course.courseId == courseId }?.entry
  if (foundCourse != null) {
    Scaffold(
      contentWindowInsets = WindowInsets(0.dp),
      topBar = {
        TopAppBar(title = { Text(text = foundCourse.course[0].title) },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary
          ),
          navigationIcon = {
            IconButton(onClick = {
              navController.popBackStack()
            }) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          })
      }) { innerPadding ->
      Column(
        Modifier
          .padding(innerPadding)
          .padding(horizontal = 8.dp)
      ) {
        var description = "¯\\_(ツ)_/¯"
        if (foundCourse.course[0].description != null) {
          description = foundCourse.course[0].description!!
        }
        val rowScrollState = rememberScrollState()
        Row(
          Modifier
            .padding(start = 6.dp)
            .horizontalScroll(rowScrollState)
            .offset(y = (-4).dp)
        ) {
          SuggestionChip(
            label = { Text(text = "${foundCourse.course[0].subject}${foundCourse.course[0].crse}") },
            onClick = { },
            modifier = Modifier.padding(
              end = 4.dp
            )
          )
          if (foundCourse.course[0].offered.isNotEmpty()) {
            val offeredSem = StringBuilder()
            for (i in foundCourse.course[0].offered) {
              offeredSem.append("${i.lowercase().replaceFirstChar(Char::titlecase)}, ")
            }
            SuggestionChip(
              label = {
                Text(
                  text = offeredSem.toString().substring(
                    0,
                    offeredSem.length - 2
                  )
                )
              },
              onClick = { },
              modifier = Modifier.padding(
                end = 4.dp
              )
            )
          }

          SuggestionChip(
            label = {
              Text(
                text = "${
                  if (foundCourse.course[0].maxCredits == foundCourse.course[0].minCredits) DecimalFormat(
                    "0.#"
                  ).format(foundCourse.course[0].maxCredits) else {
                    "${DecimalFormat("0.#").format(foundCourse.course[0].minCredits)} - ${
                      DecimalFormat(
                        "0.#"
                      ).format(foundCourse.course[0].maxCredits)
                    }"
                  }
                } Credit${if (foundCourse.course[0].maxCredits > 1) "s" else ""}"
              )
            },
            onClick = { },
            modifier = Modifier.padding(
              end = 4.dp
            )
          )
        }
        Text(
          text = "Course Description:",
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 4.dp)
        )
        ExpandableCard(
          description = description
        )

        HorizontalDivider(
          Modifier
            .padding(vertical = 8.dp)
            .padding(top = 4.dp)
        )
        Text(
          text = "Course Sections:",
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(
            start = 4.dp,
            bottom = 4.dp
          )
        )
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          itemsIndexed(items = foundCourse.sections.filter { section -> section?.deletedAt == null },
            key = { _, item -> item!!.id }) { _, item ->
            val sectionInstructor =
              semesterViewModel.instructorList.filter { instructor -> item!!.instructors.contains(SectionInstructors(instructor.id)) }
            SectionItem(
              section = item!!,
              sectionInstructor
            )
          }
        }
      }

    }
  }
}

@Composable
private fun ExpandableCard(
  description: String,
  descriptionFontSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
  descriptionFontWeight: FontWeight = FontWeight.Normal,
  descriptionMaxLines: Int = 3,
  shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
  var expandedState by remember { mutableStateOf(false) }
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize(
        animationSpec = tween(
          durationMillis = 300,
          easing = LinearOutSlowInEasing
        )
      )
      .padding(4.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    shape = shape,
    onClick = {
      expandedState = !expandedState
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      if (!expandedState) {
        Text(
          text = description,
          fontSize = descriptionFontSize,
          fontWeight = descriptionFontWeight,
          maxLines = descriptionMaxLines,
          overflow = TextOverflow.Ellipsis,
        )
      } else {
        Text(
          text = description,
          fontSize = descriptionFontSize,
          fontWeight = descriptionFontWeight,
        )
      }
    }
  }
}

