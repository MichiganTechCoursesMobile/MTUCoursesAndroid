package com.mtucoursesmobile.michigantechcourses.components.sections

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImagePainter
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
        .height(240.dp)
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (painter?.state is AsyncImagePainter.State.Success) {
            Image(
              modifier = Modifier
                .padding(end = 8.dp)
                .size(60.dp)
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
          Column {
            Text(
              text = instructor.fullName,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = instructor.departments[0]
            )
          }
        }
        if (instructor.email != null) {
          OutlinedButton(onClick = {
            context.sendMail(to = instructor.email!!)
          }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Send Email")
            }
          }
        }
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