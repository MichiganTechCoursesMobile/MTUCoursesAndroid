package com.mtucoursesmobile.michigantechcourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
        Scaffold(
          topBar = {
            TopAppBar(colors = topAppBarColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              titleContentColor = MaterialTheme.colorScheme.primary
            ),
              title = {
                Text(
                  text = "Title",
                )
              })
          },
          bottomBar = {
            BottomAppBar(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.primary,
            ) {
              Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Bottom"
              )
            }
          },
          floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
              Icon(
                Icons.Default.Add,
                contentDescription = "Add"
              )
            }
          }) { innerPadding ->
          Column(modifier = Modifier.padding(innerPadding)) {
            ModalBottomSheet(onDismissRequest = { }) {
              Text(text = "Hi")
            }
          }
        }
      }
    }
  }
}