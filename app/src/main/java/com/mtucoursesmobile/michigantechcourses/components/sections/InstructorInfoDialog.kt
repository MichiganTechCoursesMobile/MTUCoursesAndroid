package com.mtucoursesmobile.michigantechcourses.components.sections

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Paint.Style
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImagePainter
import com.mtucoursesmobile.michigantechcourses.R
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorInfoDialog(
  showInfoDialog: MutableState<Boolean>, instructor: MTUInstructor,
  painter: AsyncImagePainter? = null
) {
  val instructorNames = instructor.fullName.split(" ").toList()
  val context = LocalContext.current
  Dialog(onDismissRequest = { showInfoDialog.value = false }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column {
        Column(
          modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          if (painter?.state is AsyncImagePainter.State.Success) {
            Image(
              modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
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
          Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = instructor.fullName,
              fontWeight = FontWeight.Bold,
              fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Text(
              text = instructor.departments[0],
              fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
          }
        }
        HorizontalDivider()
        if (instructor.phone != null || instructor.email != null) {
          ElevatedCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)

          ) {
            Column(
              modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
            ) {
              Text(
                "Contact info",
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                  .padding(horizontal = 6.dp)
                  .padding(bottom = 4.dp)
              )
              if (instructor.email != null) {
                ElevatedCard(
                  onClick = {
                    context.sendMail(to = instructor.email!!)
                  },
                  elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Outlined.Email,
                      contentDescription = "Email",
                      modifier = Modifier
                        .padding(
                          start = 4.dp,
                          end = 8.dp
                        )
                        .size(18.dp)
                    )
                    Text(
                      instructor.email!!,
                      textAlign = TextAlign.Start,
                      style = MaterialTheme.typography.labelMedium,
                      modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .wrapContentHeight()
                    )
                  }
                }
              }
              if (instructor.phone != null) {
                ElevatedCard(
                  onClick = {
                    context.dial(phone = instructor.phone!!)
                  },
                  elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Outlined.Phone,
                      contentDescription = "Phone",
                      modifier = Modifier
                        .padding(
                          start = 4.dp,
                          end = 8.dp
                        )
                        .size(18.dp)
                    )
                    Text(
                      instructor.phone!!,
                      textAlign = TextAlign.Start,
                      style = MaterialTheme.typography.labelMedium,
                      modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .wrapContentHeight()
                    )
                  }
                }
              }

            }
          }
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth(0.5f)
              .padding(end = 4.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(4.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                Icons.Outlined.Speed,
                contentDescription = "Avg Difficulty",
                modifier = Modifier
                  .align(Alignment.CenterHorizontally)
                  .size(24.dp)
              )
              Text(
                "Avg Difficulty",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
              Text(
                "${(instructor.averageDifficultyRating.toDouble() * 100)}%",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 4.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(4.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                Icons.Outlined.EmojiEvents,
                contentDescription = "Avg Rating",
                modifier = Modifier
                  .align(Alignment.CenterHorizontally)
                  .size(24.dp)
              )
              Text(
                "Avg Rating",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
              Text(
                "${(instructor.averageRating.toDouble() * 100)}%",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )

            }
          }
        }

      }
      TextButton(
        onClick = { showInfoDialog.value = false },
        modifier = Modifier
          .align(Alignment.End)
          .padding(
            end = 8.dp,
            top = 8.dp
          )
      ) {
        Text("Dismiss")
      }

    }
  }
}

fun Context.sendMail(to: String) {
  try {
    val intent = Intent(Intent.ACTION_MAIN).apply {
      addCategory(Intent.CATEGORY_APP_EMAIL)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      putExtra(
        Intent.EXTRA_EMAIL,
        arrayOf(to)
      )
    }
    startActivity(intent)
  } catch (e: ActivityNotFoundException) {
    // TODO: Handle case where no email app is available
  } catch (t: Throwable) {
    // TODO: Handle potential other type of exceptions
  }
}

fun Context.dial(phone: String) {
  try {
    val intent = Intent(
      Intent.ACTION_DIAL,
      Uri.fromParts(
        "tel",
        phone,
        null
      )
    )
    startActivity(intent)
  } catch (t: Throwable) {
    // TODO: Handle potential exceptions
  }
}
