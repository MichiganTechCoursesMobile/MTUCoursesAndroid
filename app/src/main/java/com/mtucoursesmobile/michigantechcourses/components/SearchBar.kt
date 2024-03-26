package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel

@Composable
fun ExpandableSearchView(
  courseFilterViewModel: CourseFilterViewModel,
  onSearchDisplayClosed: () -> Unit,
  modifier: Modifier = Modifier,
  expandedInitially: Boolean = false,
  tint: Color = MaterialTheme.colorScheme.primary,
  expanded: Boolean,
  onExpandedChanged: (Boolean) -> Unit
) {
  val searchDisplay = courseFilterViewModel.searchBarValue
  val onSearchDisplayChanged: (String) -> Unit = {courseFilterViewModel.searchBarValue.value = it}

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
  searchDisplay: MutableState<String>,
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
        searchDisplay.value,
        TextRange(searchDisplay.value.length)
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
        imageVector = Icons.Filled.ArrowBack,
        contentDescription = "Back",
        tint = tint
      )
    }
    TextField(
      singleLine = true,
      value = textFieldValue,
      onValueChange = {
        textFieldValue = it
        onSearchDisplayChanged(it.text)
      },
      modifier = Modifier
        .fillMaxWidth()
        .focusRequester(textFieldFocusRequester)
        .padding(end = 4.dp),
      label = {
        Text(
          text = "Course Search",
          color = tint
        )
      },
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent
      ),
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
      ),
      keyboardActions = KeyboardActions(
        onDone = {
          focusManager.clearFocus()
        }
      )
    )
  }
}
