package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections

@Composable
fun SectionItem(section: MTUSections, instructors: List<MTUInstructor>) {
  var expandedState by remember { mutableStateOf(false) }
  val rotationState by animateFloatAsState(
    targetValue = if (expandedState) 180f else 0f,
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
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    shape = RoundedCornerShape(12.dp),
    onClick = {
      expandedState = !expandedState
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Section Time Here",
          Modifier.weight(7f)
        )
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = section.section
          )
          IconButton(
            modifier = Modifier
              .padding(start = 8.dp)
              .rotate(rotationState),
            onClick = {
              /* TODO */
            }) {
            Icon(
              imageVector = Icons.Outlined.AddCircleOutline,
              contentDescription = "Add to Basket"
            )
          }
        }

      }
      AnimatedVisibility(
        visible = expandedState,
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
        }
      ) {
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
              val instructorNames = instructor.fullName.split(" ").toList()
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
              ) {
                if (instructor.thumbnailURL == null) {
                  PlaceHolderAvatar(
                    id = instructor.id.toString(),
                    firstName = instructorNames.first(),
                    lastName = instructorNames.last(),
                    modifier = Modifier.padding(end = 8.dp)
                  )
                } else {
                  val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current).data(instructor.thumbnailURL)
                      .size(Size.ORIGINAL).build()
                  )
                  if (painter.state is AsyncImagePainter.State.Success) {
                    Image(
                      modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp)
                        .clip(shape = CircleShape),
                      painter = painter,
                      contentDescription = instructor.fullName
                    )
                  } else {
                    PlaceHolderAvatar(
                      id = instructor.id.toString(),
                      firstName = instructorNames.first(),
                      lastName = instructorNames.last(),
                      modifier = Modifier.padding(end = 8.dp)
                    )
                  }
                }
                Text(
                  modifier = Modifier,
                  text = instructor.fullName,
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
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
      }
    }
  }
}