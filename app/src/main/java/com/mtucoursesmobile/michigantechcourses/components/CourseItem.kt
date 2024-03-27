package com.mtucoursesmobile.michigantechcourses.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import java.text.DecimalFormat


@Composable
fun CourseItem(item: MTUCoursesEntry, navController: NavController) {
  ListItem(
    overlineContent = {
      Text(
        text = item.entry.course[0].title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(top = 4.dp, start = 1.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    headlineContent = {
      Row () {
        SuggestionChip(
          label = { Text(text = "${item.entry.course[0].subject}${item.entry.course[0].crse}") },
          onClick = {},
          modifier = Modifier
            .padding(
              end = 4.dp
            )
        )
        SuggestionChip(
          label = {
            Text(
              text =
              "${
                if (item.entry.course[0].maxCredits == item.entry.course[0].minCredits) DecimalFormat("0.#").format(item.entry.course[0].maxCredits) else {
                  "${DecimalFormat("0.#").format(item.entry.course[0].minCredits)} - ${DecimalFormat("0.#").format(item.entry.course[0].maxCredits)}"
                }
              } Credit${if (item.entry.course[0].maxCredits > 1) "s" else ""}"
            )
          },
          onClick = {},
          modifier = Modifier
            .padding(
              end = 4.dp
            )
        )
        SuggestionChip(
          label = { Text(text = "${item.entry.sections.filter { section -> section?.deletedAt == null }.size} Sections") },
          onClick = {},
          modifier = Modifier
            .padding(
              end = 4.dp
            )
        )
      }
    },
    trailingContent = {
      Icon(
        Icons.AutoMirrored.Filled.ArrowRight,
        contentDescription = "Go to Course Detail",
        modifier = Modifier.padding()
      )
    },
    tonalElevation = 4.dp,
    modifier = Modifier
      .padding(horizontal = 10.dp)
      .padding(vertical = 8.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable {
        navController.navigate("courseDetail/${item.courseId}") {

          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          // Restore state when reselecting a previously selected item
          restoreState = true

        }
      },
  )
}