package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
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


@Composable
fun CourseItem(item: MTUCoursesEntry, navController: NavController) {
  ListItem(
    headlineContent = {
      Text(
        text = "${item.entry.course[0].subject}${item.entry.course[0].crse} - ${item.entry.course[0].title}",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Left,
      )
    },
    supportingContent = {
      Row {
        SuggestionChip(
          label = { Text(text = "Hi") },
          onClick = {},
          modifier = Modifier.padding(end = 4.dp)
        )
        SuggestionChip(
          label = { Text(text = "Hi") },
          onClick = {},
        )
      }
    },
    trailingContent = {
      Icon(
        Icons.AutoMirrored.Filled.ArrowRight,
        contentDescription = "Go to funny"
      )
    },
    tonalElevation = 4.dp,
    modifier = Modifier
      .padding(horizontal = 10.dp)
      .height(90.dp)
      .clip(RoundedCornerShape(16.dp)),
  )

  ElevatedCard(
    elevation = CardDefaults.cardElevation(
      defaultElevation = 4.dp
    ),
    modifier = Modifier
      .fillMaxWidth()
      .height(110.dp)
      .padding(10.dp),

    ) {
    Text(
      text = "${item.entry.course[0].subject}${item.entry.course[0].crse} - ${item.entry.course[0].title}",
      modifier = Modifier
        .padding(
          horizontal = 10.dp
        )
        .paddingFromBaseline(
          top = 30.dp,
        ),
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp,
      textAlign = TextAlign.Left,
    )
    Row {
      SuggestionChip(
        label = { Text(text = "Hi") },
        onClick = {},
        modifier = Modifier.padding(start = 10.dp)
      )
      SuggestionChip(
        label = { Text(text = "Hi") },
        onClick = {},
        modifier = Modifier.padding(start = 4.dp)
      )
    }
  }
}