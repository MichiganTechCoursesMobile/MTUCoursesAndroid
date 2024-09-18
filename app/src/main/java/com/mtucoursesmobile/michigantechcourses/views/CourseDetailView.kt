package com.mtucoursesmobile.michigantechcourses.views

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.ErrorOutline
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.SectionInstructors
import com.mtucoursesmobile.michigantechcourses.components.LoadingSpinnerAnimation
import com.mtucoursesmobile.michigantechcourses.components.sections.SectionItem
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import java.text.DecimalFormat
import java.util.Locale

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun CourseDetailView(
  courseViewModel: MTUCoursesViewModel,
  basketViewModel: BasketViewModel,
  navController: NavController,
  courseId: String?,
  db: BasketDB
) {
  val foundCourse =
    courseViewModel.courseList[courseId]
  if (foundCourse == null) {
    navController.popBackStack()
    return
  }
  val passFailDrop = courseViewModel.failList["${foundCourse.subject}${foundCourse.crse}"]
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(title = { Text(text = foundCourse.title) },
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
      if (foundCourse.description != null) {
        description = foundCourse.description
      }
      val rowScrollState = rememberScrollState()
      Row(
        Modifier
          .padding(start = 6.dp)
          .horizontalScroll(rowScrollState)
          .offset(y = (-4).dp)
      ) {
        SuggestionChip(
          label = { Text(text = "${foundCourse.subject}${foundCourse.crse}") },
          onClick = { },
          modifier = Modifier.padding(
            end = 4.dp
          )
        )
        if (foundCourse.offered.isNotEmpty()) {
          val offeredSem = StringBuilder()
          for (i in foundCourse.offered) {
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
                if (foundCourse.maxCredits == foundCourse.minCredits) DecimalFormat(
                  "0.#"
                ).format(foundCourse.maxCredits) else {
                  "${DecimalFormat("0.#").format(foundCourse.minCredits)} - ${
                    DecimalFormat(
                      "0.#"
                    ).format(foundCourse.maxCredits)
                  }"
                }
              } Credit${if (foundCourse.maxCredits > 1) "s" else ""}"
            )
          },
          onClick = { },
          modifier = Modifier.padding(
            end = 4.dp
          )
        )
      }
      if (passFailDrop != null) {
        val ctx = LocalContext.current
        val statsRowState = rememberScrollState()
        var sumOfTotals = 0.0
        var sumOfAvgDropped = 0.0
        var sumOfAvgFailed = 0.0
        for (sem in passFailDrop) {
          sumOfTotals += sem.total
          sumOfAvgDropped += sem.dropped
          sumOfAvgFailed += sem.failed
        }
        val avgDropped = (sumOfAvgDropped / sumOfTotals) * 100
        val avgFailed = (sumOfAvgFailed / sumOfTotals) * 100
        val avgSize = sumOfTotals / passFailDrop.size
        Row(
          Modifier
            .padding(start = 6.dp)
            .horizontalScroll(statsRowState)
            .offset(y = (-12).dp)
        ) {
          SuggestionChip(
            modifier = Modifier.padding(end = 4.dp),
            onClick = {
              Toast.makeText(
                ctx,
                "Average Dropped is ${
                  String.format(
                    Locale.getDefault(),
                    "%.2f",
                    avgDropped
                  )
                }%",
                Toast.LENGTH_SHORT
              ).show()
            },
            label = {
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Outlined.ArrowDownward,
                  contentDescription = "Average Dropped"
                )
                Text(
                  text = " ${
                    String.format(
                      Locale.getDefault(),
                      "%.2f",
                      avgDropped
                    )
                  }%"
                )
              }
            }
          )
          SuggestionChip(
            modifier = Modifier.padding(end = 4.dp),
            onClick = {
              Toast.makeText(
                ctx,
                "Average Failed is ${
                  String.format(
                    Locale.getDefault(),
                    "%.2f",
                    avgFailed
                  )
                }%",
                Toast.LENGTH_SHORT
              ).show()
            },
            label = {
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Outlined.ErrorOutline,
                  contentDescription = "Average Failed"
                )
                Text(
                  text = " ${
                    String.format(
                      Locale.getDefault(),
                      "%.2f",
                      avgFailed
                    )
                  }%"
                )
              }
            }
          )
          SuggestionChip(
            modifier = Modifier.padding(end = 4.dp),
            onClick = {
              Toast.makeText(
                ctx,
                "Average Class Size is ${
                  avgSize.fastRoundToInt()
                }",
                Toast.LENGTH_SHORT
              ).show()
            },
            label = {
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Outlined.CropFree,
                  contentDescription = "Average Size"
                )
                Text(text = " ${avgSize.fastRoundToInt()}")
              }
            }
          )
        }

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
      AnimatedContent(
        targetState = courseViewModel.sectionList.isEmpty(),
        label = "Sections"
      ) { state ->
        if (state) {
          LoadingSpinnerAnimation(innerPadding = innerPadding)
        } else {
          val sections = courseViewModel.sectionList[courseId]
          if (sections != null) {
            val expandedList: MutableMap<String, MutableState<Boolean>> =
              remember { mutableMapOf() }
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              itemsIndexed(items = sections.sortedBy { section -> section.section }
                .filter { section -> section.deletedAt == null },
                key = { _, item -> item.id }) { index, item ->
                val sectionInstructor =
                  courseViewModel.instructorList.filter { instructor ->
                    item.instructors.contains(
                      SectionInstructors(instructor.key)
                    )
                  }
                if (expandedList[item.id] == null) {
                  expandedList[item.id] =
                    remember { mutableStateOf((index == 0 && sections.size <= 4)) }
                }
                val expanded = expandedList[item.id]
                SectionItem(
                  basketViewModel,
                  section = item,
                  sectionInstructor,
                  courseViewModel.buildingList,
                  courseViewModel.currentSemester,
                  db,
                  expanded!!
                )
              }
            }
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
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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

