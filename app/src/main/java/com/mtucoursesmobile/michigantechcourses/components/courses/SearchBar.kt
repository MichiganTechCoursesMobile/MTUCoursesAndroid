package com.mtucoursesmobile.michigantechcourses.components.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableSearchView(
  searchDisplay: String,
  onSearchDisplayChanged: (String) -> Unit,
  onSearchDisplayClosed: () -> Unit,
  modifier: Modifier = Modifier,
  tint: Color = MaterialTheme.colorScheme.primary,
  expanded: Boolean,
  onExpandedChanged: (Boolean) -> Unit
) {

  if (expanded) {
    ExpandedSearchView(
      searchDisplay = searchDisplay,
      onSearchDisplayChanged = onSearchDisplayChanged,
      onSearchDisplayClosed = onSearchDisplayClosed,
      onExpandedChanged = onExpandedChanged,
      modifier = modifier,
      tint = tint
    )
  }
}

@Composable
fun ExpandedSearchView(
  searchDisplay: String,
  onSearchDisplayChanged: (String) -> Unit,
  onSearchDisplayClosed: () -> Unit,
  onExpandedChanged: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  tint: Color = MaterialTheme.colorScheme.primary
) {
  val focusManager = LocalFocusManager.current

  val textFieldFocusRequester = remember { FocusRequester() }

  SideEffect {
    textFieldFocusRequester.requestFocus()
  }

  var textFieldValue by remember {
    mutableStateOf(
      TextFieldValue(
        searchDisplay, TextRange(searchDisplay.length)
      )
    )
  }

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = {
      onExpandedChanged(false)
      onSearchDisplayClosed()
    }) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = tint
      )
    }
    OutlinedTextField(singleLine = true, value = textFieldValue, onValueChange = {
      textFieldValue = it
      onSearchDisplayChanged(it.text)
    }, modifier = Modifier
      .fillMaxWidth()
      .focusRequester(textFieldFocusRequester)
      .padding(
        end = 16.dp, bottom = 6.dp
      )
      .height(64.dp), textStyle = TextStyle(fontSize = 20.sp), label = {
      Text(
        text = "Course Search", color = tint
      )
    }, colors = TextFieldDefaults.colors(
      unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent
    ), keyboardOptions = KeyboardOptions(
      imeAction = ImeAction.Done
    ), keyboardActions = KeyboardActions(onDone = {
      focusManager.clearFocus()
    }), trailingIcon = {
      IconButton(onClick = {
        textFieldValue = TextFieldValue("")
        onSearchDisplayChanged("")
      }) {
        Icon(
          imageVector = Icons.Filled.Clear, contentDescription = "Clear", tint = tint
        )
      }
    })

  }
}
