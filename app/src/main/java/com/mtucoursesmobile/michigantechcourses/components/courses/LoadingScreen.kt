package com.mtucoursesmobile.michigantechcourses.components.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(
  innerPadding: PaddingValues, courseStatus: Int, sectionStatus: Int, semesterStatus: Int,
  instructorStatus: Int, buildingStatus: Int, dropStatus: Int, retry: () -> Unit
) {
  Column(
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Semesters")
      when (semesterStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Courses")
      when (courseStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Sections")
      when (sectionStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Instructors")
      when (instructorStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Buildings")
      when (buildingStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(32.dp)
    ) {
      Text("Loading Drop Rates")
      when (dropStatus) {
        0 -> CircularProgressIndicator(
          modifier = Modifier
            .padding(4.dp)
            .width(24.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
        )

        1 -> Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Loaded",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(4.dp)
        )

        2 -> Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
    if (
      (courseStatus == 2 || sectionStatus == 2 || semesterStatus == 2 || instructorStatus == 2 || buildingStatus == 2 || dropStatus == 2) &&
      !(courseStatus == 0 || sectionStatus == 0 || semesterStatus == 0 || instructorStatus == 0 || buildingStatus == 0 || dropStatus == 0)
    ) {
      TextButton(
        onClick = { retry() },
        colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.error
        )
      ) {
        Text("Retry")
      }
    }

  }
}