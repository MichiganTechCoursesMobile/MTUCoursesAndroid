package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections

@Composable
fun SectionItem(section: MTUSections) {
  ListItem(
    headlineContent = { Text(text = section.crn) },
    modifier = Modifier
      .padding(horizontal = 4.dp)
      .padding(vertical = 8.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable {
        /* TODO */
      },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
      overlineColor = MaterialTheme.colorScheme.onSurface,
      headlineColor = MaterialTheme.colorScheme.onSurface
    )
  )
}