package com.mtucoursesmobile.michigantechcourses.components.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun CalendarTimes() {
  Column {
    Box(Modifier.padding(top = 52.dp)) {
      Box(
        modifier = Modifier
          .width(50.dp)
          .height(0.25.dp)
          .background(MaterialTheme.colorScheme.onSurface)
          .align(Alignment.BottomCenter)
      )
    }
    val items = (7..22).toList()
    LazyColumn(
      modifier = Modifier
        .width(50.dp),
      userScrollEnabled = false
    ) {
      itemsIndexed(
        items,
        key = { index, _ -> index }) { _, time ->
        val borderColor = MaterialTheme.colorScheme.outline
        Canvas(
          modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
        ) {
          val strokeWidth = 0.15.dp.toPx()

          drawRect(
            color = borderColor,
            style = Stroke(strokeWidth)
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .fillParentMaxHeight(1.toFloat() / items.size.toFloat())
            .background(Color.Transparent)
            .zIndex(2f),
          contentAlignment = Alignment.TopEnd
        ) {
          if (time < 12) {
            Text(text = "$time am")
          } else if (time == 12) {
            Text(text = "$time pm")
          } else {
            Text(text = "${time - 12} pm")
          }
        }
      }
    }
  }
}