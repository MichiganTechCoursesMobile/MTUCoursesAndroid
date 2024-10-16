package com.mtucoursesmobile.michigantechcourses.components.baskets

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.mtucoursesmobile.michigantechcourses.components.sections.InstructorInfoDialog
import com.mtucoursesmobile.michigantechcourses.components.sections.PlaceHolderAvatar
import com.mtucoursesmobile.michigantechcourses.utils.sectionDateFormatter
import com.mtucoursesmobile.michigantechcourses.utils.sectionTimeFormatter
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsModelProvider
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat


/*
* Navigate to course detail from a basket item
*/
fun navToCourse(
  courseNavController: NavController,
  courseId: String
) {
  courseNavController.navigate("courseDetail/${courseId}") {

    //Navigate to course detail
    popUpTo(courseNavController.graph.findStartDestination().id) {
      saveState = true
    }
    // Restore state when reselecting a previously selected item
    restoreState = true

  }
}

/*
* Individual Item (Section) found in a Basket
*/

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasketItem(
  section: MTUSections,
  course: MTUCourses?,
  removeFromBasket: (MTUSections, CurrentSemester, SnackbarHostState) -> Unit,
  currentSemester: CurrentSemester,
  navController: NavController,
  courseNavController: NavController,
  instructors: Map<Number, MTUInstructor>,
  buildings: Map<String, MTUBuilding>,
  snackbarHostState: SnackbarHostState
) {
  val scope = rememberCoroutineScope()
  val dismissThreshold = 0.50f
  val currentFraction = remember { mutableFloatStateOf(0f) }
  AnimatedVisibility(
    visible = course != null, //Prevent issues with null courses
    enter = slideInVertically(),
    exit = fadeOut()
  ) {
    // Handles the logic for the swiping animation
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
      var swipped = false
      var delete = false
      var view = false

      // Swipe to Delete Section from basket, or to view section's detailed course view
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
      // Delete section from basket
      if (delete) {
        scope.launch {
          delay(250)
          removeFromBasket(
            section,
            currentSemester,
            snackbarHostState
          )
        }
      }
      // Navigate to course detail
      if (view) {
        scope.launch {
          // Ensure that the Course page is at its default state to prevent issues
          courseNavController.navigate("courseList")

          //Navigate to the Main Course List
          navController.navigate("Courses") {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
            // Restore state when reselecting a previously selected item
            restoreState = true
          }

          // Quickly navigate to the selected course detail
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
          },
          label = "Change the color based on the direction you slide"
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
          if (dismissThreshold == currentFraction.floatValue) 0.75f else 1f,
          label = "Threshold for sliding animation"
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

      // Individual Basket Item
      ListItem(
        overlineContent = {
          Row {
            Box(Modifier.weight(7f)) {
              if (instructors.isNotEmpty()) {
                val instructor = instructors[instructors.keys.first()]
                if (instructor != null) {
                  val showAdditionalInstructorInfo = remember { mutableStateOf(false) }
                  val showInstructorInfo =
                    !instructor.rmpId.isNullOrBlank() && (instructor.averageRating.toDouble() != 0.0) && (instructor.averageDifficultyRating.toDouble() != 0.0)
                  var painter: AsyncImagePainter? = null
                  val instructorNames = instructor.fullName.split(" ").toList()
                  Box(/*
                    * Having this if statement allows users to click the
                    * instructor and close the card if they have no info
                    * */
                    modifier = if (showInstructorInfo) {
                      Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = true,
                          onClick = { showAdditionalInstructorInfo.value = true })
                    } else {
                      Modifier.clip(RoundedCornerShape(10.dp))
                    }
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      if (instructor.thumbnailURL == null) {
                        PlaceHolderAvatar(
                          id = instructor.id.toString(),
                          firstName = instructorNames.first(),
                          lastName = instructorNames.last(),
                          modifier = Modifier.padding(end = 8.dp),
                          size = 30.dp,
                          textStyle = MaterialTheme.typography.bodySmall
                        )
                      } else {
                        painter = rememberAsyncImagePainter(
                          model = ImageRequest.Builder(LocalContext.current)
                            .data(instructor.thumbnailURL).size(Size.ORIGINAL).build()
                        )
                        if (painter?.state is AsyncImagePainter.State.Success) {
                          Image(
                            modifier = Modifier
                              .padding(end = 8.dp)
                              .size(30.dp)
                              .clip(shape = CircleShape),
                            painter = painter!!,
                            contentDescription = instructor.fullName
                          )
                        } else {
                          PlaceHolderAvatar(
                            id = instructor.toString(),
                            firstName = instructorNames.first(),
                            lastName = instructorNames.last(),
                            modifier = Modifier.padding(end = 8.dp),
                            size = 30.dp
                          )
                        }
                      }
                      // We use a FlowRow here to prevent narrow displays from experiencing issues
                      FlowRow(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                          modifier = Modifier.padding(end = 2.dp),
                          text = instructor.fullName,
                          fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                        // If there is more than 1 instructor teacher a section
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
                  // Shows the additional instructor info
                  when {
                    showAdditionalInstructorInfo.value -> {
                      InstructorInfoDialog(
                        showAdditionalInstructorInfo,
                        instructor,
                        painter
                      )
                    }
                  }
                }
              } else { // Instructor is unknown
                Row(verticalAlignment = Alignment.CenterVertically) {
                  PlaceHolderAvatar(
                    id = "¯\\_(ツ)_/¯",
                    firstName = "?",
                    lastName = "",
                    modifier = Modifier.padding(end = 8.dp),
                    size = 30.dp
                  )
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      modifier = Modifier.padding(end = 2.dp),
                      text = "¯\\_(ツ)_/¯",
                      fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }
            Badge(
              modifier = Modifier.padding(top = 4.dp),
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
              containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = sectionTimeFormatter(section),
                style = MaterialTheme.typography.labelMedium
              )
            }
          }
        },
        headlineContent = {
          Text(
            text = "${course?.subject}${course?.crse} - ${course?.title}",
            modifier = Modifier
              .padding(top = 4.dp)
              .offset(y = 2.dp),
            style = MaterialTheme.typography.titleSmall
          )
        },
        supportingContent = {
          val clipboardManager: ClipboardManager = LocalClipboardManager.current
          FlowRow(
            modifier = Modifier
              .offset(y = 6.dp),
            verticalArrangement = Arrangement.spacedBy((-10).dp)
          ) {
            SuggestionChip(
              onClick = { },
              label = {
                Text(
                  text = "${section.availableSeats}/${section.totalSeats}",
                )
              },
              modifier = Modifier
                .padding(end = 4.dp),
              colors = SuggestionChipDefaults.suggestionChipColors(labelColor = if (section.availableSeats.toInt() <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            )
            // Open the building in Device Map app (Google Maps, Waze, etc...)
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
              onClick = { clipboardManager.setText(AnnotatedString(section.crn)) }, //Copy CRN to clipboard
              label = { Text(text = "CRN: ${section.crn}") },
              modifier = Modifier.padding(end = 4.dp)
            )
            SuggestionChip(
              label = {
                Text(
                  text = "${
                    if (section.maxCredits == section.minCredits) DecimalFormat(
                      "0.#"
                    ).format(section.maxCredits) else {
                      "${DecimalFormat("0.#").format(section.minCredits)} - ${
                        DecimalFormat(
                          "0.#"
                        ).format(section.maxCredits)
                      }"
                    }
                  } Credit${if (section.maxCredits.toDouble() > 1) "s" else ""}"
                )
              },
              onClick = { },
              modifier = Modifier.padding(end = 4.dp)
            )
            val settingsModel: SettingsViewModel =
              viewModel(factory = SettingsModelProvider.Factory)
            val dateFormat by settingsModel.dateFormat.collectAsState()
            SuggestionChip(
              label = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "Section time",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(end = 4.dp)
                  )
                  Text(
                    text = sectionDateFormatter(
                      section,
                      dateFormat
                    )
                  )
                }
              },
              onClick = {}
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
          containerColor = MaterialTheme.colorScheme.surfaceContainer,
          overlineColor = MaterialTheme.colorScheme.onSurface,
          headlineColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
          .padding(horizontal = 10.dp)
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(12.dp))
      )
    }
  }
}