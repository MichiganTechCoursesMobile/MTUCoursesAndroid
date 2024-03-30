package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingCourseList(innerPadding: PaddingValues) {
  LazyColumn(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
    items(50) {
      ListItem(
        overlineContent = {
          Text(text = "")
        },
        headlineContent = {
          Row {
            SuggestionChip(
              label = { Text(text = "") },
              onClick = { },
              modifier = Modifier
                .padding(
                  end = 4.dp,
                  top = 25.dp
                )
                .width(90.dp)
                .shimmer()
            )
            SuggestionChip(
              label = { Text(text = "") },
              onClick = { },
              modifier = Modifier
                .padding(
                  end = 4.dp,
                  top = 25.dp
                )
                .width(90.dp)
                .shimmer()
            )
          }
        },
        modifier = Modifier
          .padding(horizontal = 10.dp)
          .padding(vertical = 8.dp)
          .height(92.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHighest)
          .shimmer(),
      )
    }
  }
}