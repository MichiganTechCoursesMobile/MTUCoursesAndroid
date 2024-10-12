package com.mtucoursesmobile.michigantechcourses.components.sections

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.utils.dateTimeFormatter

@Composable
fun SectionItem(
  currentBasketItems: SnapshotStateMap<String, MTUSections>,
  addToBasket: (MTUSections, CurrentSemester) -> Unit,
  removeFromBasket: (MTUSections, CurrentSemester, SnackbarHostState?) -> Unit,
  section: MTUSections,
  instructors: Map<Number, MTUInstructor>,
  buildings: Map<String, MTUBuilding>,
  currentSemester: CurrentSemester,
  expandedState: MutableState<Boolean>
) {
  val rotationState by animateFloatAsState(
    targetValue = if (expandedState.value) 180f else 0f,
    label = "Expand"
  )
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
    shape = RoundedCornerShape(12.dp),
    onClick = {
      expandedState.value = !expandedState.value
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          horizontal = 12.dp,
          vertical = 4.dp
        )
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(modifier = Modifier.weight(7f)) {
          ElevatedAssistChip(
            onClick = {
              expandedState.value = !expandedState.value
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Section time",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
              )
            },
            label = { Text(text = dateTimeFormatter(section)) },
            colors = AssistChipDefaults.elevatedAssistChipColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              labelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = section.section
          )
          IconButton(modifier = Modifier
            .padding(start = 8.dp)
            .rotate(rotationState),
            onClick = {
              if (currentBasketItems[section.id] == null) {
                addToBasket(
                  section,
                  currentSemester,
                )
              } else {
                removeFromBasket(
                  section,
                  currentSemester,
                  null
                )
              }

            }) {
            AnimatedContent(
              targetState = currentBasketItems[section.id] != null,
              label = "Add/Remove section from basket"
            ) {
              if (!it) {
                Icon(
                  imageVector = Icons.Outlined.AddCircleOutline,
                  contentDescription = "Add to Basket"
                )
              } else {
                Icon(
                  imageVector = Icons.Outlined.RemoveCircleOutline,
                  contentDescription = "Remove From Basket"
                )
              }

            }

          }
        }

      }
      AnimatedVisibility(visible = expandedState.value,
        enter = slideIn(
          tween(
            300,
            easing = LinearOutSlowInEasing
          )
        ) {
          IntOffset(
            0,
            -50
          )
        }) {
        Column {
          Column {
            Text(
              text = "Instructor${
                if (instructors.size > 1) {
                  "s"
                } else {
                  ""
                }
              }",
              fontSize = MaterialTheme.typography.titleMedium.fontSize,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(
                start = 4.dp,
                bottom = 4.dp
              )
            )
            if (instructors.isNotEmpty()) {
              for (instructor in instructors) {
                val instructorNames = instructor.value.fullName.split(" ").toList()
                val showAdditionalInstructorInfo = remember { mutableStateOf(false) }
                var painter: AsyncImagePainter? = null
                val showInstructorInfo =
                  (!instructor.value.rmpId.isNullOrBlank() && (instructor.value.averageRating.toDouble() != 0.0) && (instructor.value.averageDifficultyRating.toDouble() != 0.0)) || !(instructor.value.email.isNullOrBlank() && instructor.value.phone.isNullOrBlank())
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(
                    bottom = 8.dp
                  )
                ) {
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
                      if (instructor.value.thumbnailURL == null) {
                        PlaceHolderAvatar(
                          id = instructor.key.toString(),
                          firstName = instructorNames.first(),
                          lastName = instructorNames.last(),
                          modifier = Modifier.padding(end = 8.dp)
                        )
                      } else {
                        painter = rememberAsyncImagePainter(
                          model = ImageRequest.Builder(LocalContext.current)
                            .data(instructor.value.thumbnailURL).size(Size.ORIGINAL).build()
                        )
                        if (painter?.state is AsyncImagePainter.State.Success) {
                          Image(
                            modifier = Modifier
                              .padding(end = 8.dp)
                              .size(40.dp)
                              .clip(shape = CircleShape),
                            painter = painter!!,
                            contentDescription = instructor.value.fullName
                          )
                        } else {
                          PlaceHolderAvatar(
                            id = instructor.key.toString(),
                            firstName = instructorNames.first(),
                            lastName = instructorNames.last(),
                            modifier = Modifier.padding(end = 8.dp)
                          )
                        }
                      }
                      Text(
                        modifier = Modifier.padding(end = 2.dp),
                        text = instructor.value.fullName,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      if (showInstructorInfo) {
                        Icon(
                          imageVector = Icons.Outlined.Info,
                          contentDescription = "View Instructor info",
                          Modifier.padding(horizontal = 2.dp)
                        )
                      }
                    }
                  }
                }
                when {
                  showAdditionalInstructorInfo.value -> {
                    InstructorInfoDialog(
                      showAdditionalInstructorInfo,
                      instructor.value,
                      painter
                    )
                  }
                }
              }
            } else {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
              ) {
                PlaceHolderAvatar(
                  id = "¯\\_(ツ)_/¯",
                  firstName = "?",
                  lastName = "",
                  modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                  modifier = Modifier,
                  text = "¯\\_(ツ)_/¯",
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
          val clipboardManager: ClipboardManager = LocalClipboardManager.current
          val scrollState = rememberScrollState()

          Row(modifier = Modifier.horizontalScroll(scrollState)) {
            SuggestionChip(
              onClick = { /*TODO*/ },
              label = { Text(text = "${section.availableSeats}/${section.totalSeats} Seats") },
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
                onClick = { if (expandedState.value) mContext.startActivity(intent) },
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
              onClick = { if (expandedState.value) clipboardManager.setText(AnnotatedString(section.crn)) },
              label = { Text(text = "CRN: ${section.crn}") },
              modifier = Modifier.padding(end = 4.dp)
            )
          }
        }
      }
    }
  }
}