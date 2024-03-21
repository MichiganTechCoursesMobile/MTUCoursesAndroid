package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.semesterList
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class
)
@Composable
fun CourseView(db: AppDatabase, innerPadding: PaddingValues) {
  var searchBarValue by rememberSaveable { mutableStateOf("") }
  var currentSemester by remember {
    mutableStateOf(
      CurrentSemester(
        "Fall 2024",
        "2024",
        "FALL"
      )
    )
  }
  var expanded by remember { mutableStateOf(false) }
  var searching by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()
  val expandedFab by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
  }
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current
  Scaffold(modifier = Modifier.padding(innerPadding),
    topBar = {
      TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (expandedFab) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary
      ),
        title = {
          if (!searching) {
            OutlinedButton(onClick = { expanded = true }) {
              Text(currentSemester.readable)
            }
            DropdownMenu(
              expanded = expanded,
              onDismissRequest = { expanded = false }) {
              for (i in semesterList) {
                DropdownMenuItem(
                  text = { Text(i.readable) },
                  onClick = {
                    currentSemester = i
                    expanded = false
                  })
              }
            }
          } else {
            LaunchedEffect(Unit) {
              focusRequester.requestFocus()
            }
            TextField(
              value = searchBarValue,
              onValueChange = { searchBarValue = it },
              label = { Text("Course Search") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                  if (it.isFocused) {
                    keyboardController?.show()
                  }
                },
              colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background
              )
            )
          }
        },
        navigationIcon = {
          if (searching) {
            IconButton(onClick = {
              searching = false
              searchBarValue = ""
            }) {
              Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
              )
            }
          }
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          searching = true
        },
        expanded = expandedFab,
        icon = {
          Icon(
            Icons.Filled.Search,
            "Search Button"
          )
        },
        text = { Text(text = "Search") },
      )

    }) { innerPadding ->
    LazyCourseList(
      innerPadding = innerPadding,
      currentSemester = currentSemester,
      db = db,
      searchBarVal = searchBarValue,
      listState = listState
    )
  }
}