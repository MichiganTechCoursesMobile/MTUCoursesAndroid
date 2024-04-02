package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FancyTabIndicator(modifier: Modifier = Modifier) {
  Box(
    modifier
      .padding(5.dp)
      .fillMaxSize()
      .border(
        BorderStroke(
          2.dp,
          MaterialTheme.colorScheme.primary
        ),
        RoundedCornerShape(5.dp)
      )
  )
}