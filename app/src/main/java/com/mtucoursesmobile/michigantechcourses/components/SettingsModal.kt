package com.mtucoursesmobile.michigantechcourses.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.mtucoursesmobile.michigantechcourses.components.settings.ThemePicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(showSettings: DrawerState) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  fun closeDrawer() {
    scope.launch {
      showSettings.close()
    }
  }

  BackHandler(showSettings.isOpen || showSettings.isAnimationRunning) {
    closeDrawer()
  }

  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
  Scaffold(
    contentWindowInsets = WindowInsets.systemBars
      .only(WindowInsetsSides.End + WindowInsetsSides.Vertical),
    topBar = {
      CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.systemBars
          .only(WindowInsetsSides.End + WindowInsetsSides.Top),
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainer,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
          IconButton(onClick = { closeDrawer() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = "Close Settings",
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        },
        title = {
          Text(
            "Settings",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
          )
        })
    }
  ) { contentPadding ->
    val layoutDirection = LocalLayoutDirection.current
    
    Column(modifier = Modifier.padding(contentPadding)) {
      ThemePicker()
    }
  }
}