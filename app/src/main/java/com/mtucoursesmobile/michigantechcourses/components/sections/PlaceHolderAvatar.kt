package com.mtucoursesmobile.michigantechcourses.components.sections


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.mtucoursesmobile.michigantechcourses.utils.toHslColor
import kotlin.math.absoluteValue

@Composable
fun PlaceHolderAvatar(
  id: String,
  firstName: String,
  lastName: String,
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
  Box(
    modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    val color = remember(
      id,
      firstName,
      lastName
    ) {
      val name = listOf(
        firstName,
        lastName
      )
        .joinToString(separator = "")
        .uppercase()
      Color("$id / $name".toHslColor())
    }
    val initials = (firstName.take(1) + lastName.take(1)).uppercase()
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(SolidColor(color))
    }
    Text(
      text = initials,
      style = textStyle,
      color = Color.White
    )
  }
}