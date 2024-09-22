package com.mtucoursesmobile.michigantechcourses.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

// Reverses the layout of a composable
@Composable
fun ReverseLayoutDirection(content: @Composable () -> Unit) {
  val reverseDirection = when (LocalLayoutDirection.current) {
    LayoutDirection.Rtl -> LayoutDirection.Ltr
    LayoutDirection.Ltr -> LayoutDirection.Rtl
  }
  CompositionLocalProvider(LocalLayoutDirection provides reverseDirection) {
    content()
  }
}