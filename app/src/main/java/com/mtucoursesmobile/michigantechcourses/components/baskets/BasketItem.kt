package com.mtucoursesmobile.michigantechcourses.components.baskets

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.components.sections.PlaceHolderAvatar
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.utils.dateTimeFormatter
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
  courseNavController: NavController,
  instructors: Map<Number, MTUInstructor>,
  buildings: Map<String, MTUBuilding>,
  snackbarHostState: SnackbarHostState
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
          db,
          snackbarHostState
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
        navToCourse(
          courseNavController,
          course!!.id
        )
      }
    }
    swipped

  })
  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = {
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
              modifier = Modifier.scale(scale),
              tint = if (icon != Icons.Outlined.RemoveRedEye) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
        currentFraction.floatValue = dismissState.progress
      }
    }
  ) {
    AnimatedVisibility(
      visible = course != null,
      enter = slideInVertically(),
      exit = slideOutVertically()
    ) {
      ListItem(
        overlineContent = {
          Row {
            Box(Modifier.weight(7f)) {
              if (instructors.isNotEmpty()) {
                val instructor = instructors[instructors.keys.first()]
                if (instructor != null) {
                  val instructorNames = instructor.fullName.split(" ").toList()
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (instructor.thumbnailURL == null) {
                      PlaceHolderAvatar(
                        id = instructor.id.toString(),
                        firstName = instructorNames.first(),
                        lastName = instructorNames.last(),
                        modifier = Modifier.padding(end = 8.dp),
                        size = 25.dp,
                        textStyle = MaterialTheme.typography.bodySmall
                      )
                    } else {
                      val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                          .data(instructor.thumbnailURL).size(Size.ORIGINAL).build()
                      )
                      if (painter.state is AsyncImagePainter.State.Success) {
                        Image(
                          modifier = Modifier
                            .padding(end = 8.dp)
                            .size(25.dp)
                            .clip(shape = CircleShape),
                          painter = painter,
                          contentDescription = instructor.fullName
                        )
                      } else {
                        PlaceHolderAvatar(
                          id = instructor.toString(),
                          firstName = instructorNames.first(),
                          lastName = instructorNames.last(),
                          modifier = Modifier.padding(end = 8.dp),
                          size = 25.dp
                        )
                      }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        modifier = Modifier.padding(end = 2.dp),
                        text = instructor.fullName,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      if (instructors.size > 1) {
                        Badge(
                          containerColor = MaterialTheme.colorScheme.primaryContainer,
                          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                          Text(text = "+${instructors.size - 1}")
                        }
                      }
                    }
                  }
                }
              }
            }
            Badge(
              modifier = Modifier.padding(top = 4.dp),
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
              containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(text = dateTimeFormatter(section))
            }
          }
        },
        headlineContent = {
          Text(
            text = "${course?.subject}${course?.crse} - ${course?.title}",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.titleSmall
          )
        },
        supportingContent = {
          val scrollState = rememberScrollState()
          val clipboardManager: ClipboardManager = LocalClipboardManager.current
          Row(modifier = Modifier.horizontalScroll(scrollState)) {
            SuggestionChip(
              onClick = { /*TODO*/ },
              label = {
                Text(
                  text = "${section.availableSeats}/${section.totalSeats}",
                )
              },
              modifier = Modifier.padding(end = 4.dp),
              colors = SuggestionChipDefaults.suggestionChipColors(labelColor = if (section.availableSeats.toInt() <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            )
            if (section.buildingName != null && section.locationType == "PHYSICAL") {
              val mContext = LocalContext.current
              val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=${buildings[section.buildingName]?.lat},${buildings[section.buildingName]?.lon}(${buildings[section.buildingName]?.shortName})")
              )
              SuggestionChip(
                onClick = {
                  mContext.startActivity(intent)
                },
                label = { Text(text = "${buildings[section.buildingName]?.shortName} ${section.room}") },
                modifier = Modifier.padding(end = 4.dp)
              )
            } else if (section.locationType == "ONLINE") {
              SuggestionChip(
                onClick = { },
                label = { Text(text = "Online") },
                modifier = Modifier.padding(end = 4.dp)
              )
            } else {
              SuggestionChip(
                onClick = { },
                label = { Text(text = "¯\\_(ツ)_/¯") },
                modifier = Modifier.padding(end = 4.dp)
              )
            }
            SuggestionChip(
              onClick = { clipboardManager.setText(AnnotatedString(section.crn)) },
              label = { Text(text = "CRN: ${section.crn}") },
              modifier = Modifier.padding(end = 4.dp)
            )
          }
        },
        trailingContent = {
          Text(
            text = section.section,
            modifier = Modifier.padding(top = 4.dp)
          )
        },
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
}