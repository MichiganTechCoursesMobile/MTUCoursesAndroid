package com.mtucoursesmobile.michigantechcourses.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.mtucoursesmobile.michigantechcourses.R

@Composable
fun AppIcon(iconSize: Dp = 96.dp, modifier: Modifier = Modifier) {
  ResourcesCompat.getDrawable(
    LocalContext.current.resources,
    R.mipmap.ic_launcher,
    LocalContext.current.theme
  )?.let { drawable ->
    val bitmap = Bitmap.createBitmap(
      drawable.intrinsicWidth,
      drawable.intrinsicHeight,
      Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(
      0,
      0,
      canvas.width,
      canvas.height
    )
    drawable.draw(canvas)
    Image(
      // painter = painterResource(R.mipmap.ic_launcher),
      bitmap = bitmap.asImageBitmap(),
      "An image",
      modifier = modifier.requiredSize(iconSize)
    )
  }
}