package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun navToCourse(
  courseNavController: NavController,
  courseId: String
) {
  courseNavController.navigate("courseDetail/${courseId}") {

    popUpTo(courseNavController.graph.findStartDestination().id) {
      saveState = true
    }
    // Restore state when reselecting a previously selected item
    restoreState = true

  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketItem(
  section: MTUSections,
  course: MTUCourses?,
  basketViewModel: BasketViewModel,
  currentSemester: CurrentSemester,
  db: BasketDB,
  navController: NavController,
  courseNavController: NavController
) {
  val scope = rememberCoroutineScope()
  val dismissThreshold = 0.50f
  val currentFraction = remember { mutableFloatStateOf(0f) }
  val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
    var swipped = false
    var delete = false
    var view = false
    if (it == SwipeToDismissBoxValue.EndToStart) {
      if (currentFraction.floatValue >= dismissThreshold && currentFraction.floatValue < 1.0f) {
        swipped = true
        delete = true
      }
    } else if (it == SwipeToDismissBoxValue.StartToEnd) {
      if (currentFraction.floatValue >= dismissThreshold && currentFraction.floatValue < 1.0f) {
        swipped = true
        view = true
      }
    }
    if (delete) {
      scope.launch {
        delay(250)
        basketViewModel.removeFromBasket(
          section,
          currentSemester,
          db
        )
      }
    }
    if (view) {
      scope.launch {
        courseNavController.navigate("courseList")
        navController.navigate("Courses") {
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          // Restore state when reselecting a previously selected item
          restoreState = true
        }
        navToCourse(courseNavController, course!!.id)
      }
    }
    swipped

  })
  SwipeToDismissBox(
    state = dismissState, backgroundContent = {
      val color by animateColorAsState(
        when (dismissState.targetValue) {
          SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
          SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
          SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        }
      )
      val alignment = when (dismissState.targetValue) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
      }
      val icon = when (dismissState.targetValue) {
        SwipeToDismissBoxValue.StartToEnd -> Icons.Outlined.RemoveRedEye
        SwipeToDismissBoxValue.EndToStart -> Icons.Outlined.Delete
        SwipeToDismissBoxValue.Settled -> Icons.Outlined.ArrowDropDown
      }
      val scale by animateFloatAsState(
        if (dismissThreshold == currentFraction.floatValue) 0.75f else 1f
      )
      Box(
        Modifier
          .fillMaxSize()
          .padding(vertical = 8.dp)
          .background(color)
          .padding(horizontal = 20.dp),
        contentAlignment = alignment
      ) {
        Column {
          AnimatedVisibility(visible = icon != Icons.Outlined.ArrowDropDown) {
            Icon(
              icon,
              contentDescription = "Localized description",
              modifier = Modifier.scale(scale)
            )
          }
        }
        currentFraction.floatValue = dismissState.progress
      }
    }
  ) {
    ListItem(
      overlineContent = {
        Text(
          text = if (course != null) "${course.subject}${course.crse}" else "Idk the course bruh",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          textAlign = TextAlign.Left,
          modifier = Modifier.padding(top = 4.dp),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      },
      headlineContent = { Text(section.crn) },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        overlineColor = MaterialTheme.colorScheme.onSurface,
        headlineColor = MaterialTheme.colorScheme.onSurface
      ),
      modifier = Modifier
        .padding(horizontal = 10.dp)
        .padding(vertical = 8.dp)
        .clip(RoundedCornerShape(12.dp))
    )
  }
}