package com.mtucoursesmobile.michigantechcourses.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import java.text.DecimalFormat

fun navToCourse(
  navController: NavController,
  courseId: String
) {
  navController.navigate("courseDetail/${courseId}") {

    popUpTo(navController.graph.findStartDestination().id) {
      saveState = true
    }
    // Restore state when reselecting a previously selected item
    restoreState = true

  }
}

@Composable
fun CourseItem(
  item: Pair<String, MTUCourses>,
  navController: NavController,
) {
  ListItem(
    overlineContent = {
      Text(
        text = item.second.title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(top = 4.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    headlineContent = {
      Row {
        SuggestionChip(
          label = { Text(text = "${item.second.subject}${item.second.crse}") },
          onClick = {
            navToCourse(
              navController,
              item.first
            )
          },
          modifier = Modifier.padding(
            end = 4.dp
          )
        )
        SuggestionChip(
          label = {
            Text(
              text = "${
                if (item.second.maxCredits == item.second.minCredits) DecimalFormat(
                  "0.#"
                ).format(item.second.maxCredits) else {
                  "${DecimalFormat("0.#").format(item.second.minCredits)} - ${
                    DecimalFormat(
                      "0.#"
                    ).format(item.second.maxCredits)
                  }"
                }
              } Credit${if (item.second.maxCredits > 1) "s" else ""}"
            )
          },
          onClick = {
            navToCourse(
              navController,
              item.first
            )
          },
          modifier = Modifier.padding(
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

    modifier = Modifier
      .padding(horizontal = 10.dp)
      .padding(vertical = 8.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable {
        navToCourse(
          navController,
          item.first
        )
      },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
      overlineColor = MaterialTheme.colorScheme.onSurface,
      headlineColor = MaterialTheme.colorScheme.onSurface
    )
  )
}