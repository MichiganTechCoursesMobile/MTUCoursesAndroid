package com.mtucoursesmobile.michigantechcourses.components.sections

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImagePainter
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import java.util.Locale

@Composable
fun InstructorInfoDialog(
  showInfoDialog: MutableState<Boolean>,
  instructor: MTUInstructor,
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
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            if (instructor.departments.isNotEmpty()) {
              Text(
                text = instructor.departments[0],
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
              )
            }
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
                .fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                "Contact info",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                  .padding(horizontal = 6.dp)
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
                          end = 8.dp,
                        )
                        .size(24.dp)
                    )
                    Text(
                      instructor.email!!,
                      textAlign = TextAlign.Start,
                      style = MaterialTheme.typography.labelLarge,
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
                        .size(24.dp)
                    )
                    Text(
                      instructor.phone!!,
                      textAlign = TextAlign.Start,
                      style = MaterialTheme.typography.labelLarge,
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
        } else {
          Spacer(modifier = Modifier.height(12.dp))
        }
        if (!instructor.rmpId.isNullOrBlank() && (instructor.averageRating.toDouble() != 0.0) && (instructor.averageDifficultyRating.toDouble() != 0.0)) {
          Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            ElevatedCard(
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
                  "${
                    String.format(
                      Locale.getDefault(),
                      "%.2f",
                      (instructor.averageDifficultyRating.toDouble() * 100)
                    )
                  }%",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
            ElevatedCard(
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
                  "${
                    String.format(
                      Locale.getDefault(),
                      "%.2f",
                      (instructor.averageRating.toDouble() * 100)
                    )
                  }%",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )

              }
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
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.setData(Uri.parse("mailto:$to"))
    startActivity(intent)
  } catch (e: ActivityNotFoundException) {
    Toast.makeText(
      this,
      "No email clients found",
      Toast.LENGTH_LONG
    ).show()
  } catch (t: Throwable) {
    Toast.makeText(
      this,
      "Something went wrong",
      Toast.LENGTH_LONG
    ).show()
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
    Toast.makeText(
      this,
      "Something went wrong",
      Toast.LENGTH_LONG
    ).show()
  }
}
