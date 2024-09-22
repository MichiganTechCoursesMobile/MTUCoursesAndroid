package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import kotlinx.coroutines.launch

@Composable
fun EditBasketDialog(
  showEditDialog: MutableState<Boolean>,
  semesterBaskets: SnapshotStateList<CourseBasket>,
  currentSemester: CurrentSemester,
  currentBasketIndex: Int,
  refreshBaskets: (CurrentSemester) -> Unit
) {
  var renameText by remember { mutableStateOf("") }
  val scope = rememberCoroutineScope()
  val textFieldFocusRequester = remember { FocusRequester() }
  var textFieldError by remember { mutableStateOf(false) }
  Dialog(onDismissRequest = { showEditDialog.value = false }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()

        .padding(16.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "Edit ${semesterBaskets[currentBasketIndex].name}'s name?",
          style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
          modifier = Modifier
            .padding(top = 24.dp)
            .focusRequester(textFieldFocusRequester),
          value = renameText,
          onValueChange = {
            renameText = it
            textFieldError = false
          },
          isError = textFieldError,
          supportingText = {
            if (renameText.length > 16) {
              Text(
                text = "Name cannot be greater than 16 characters",
                color = MaterialTheme.colorScheme.error
              )
            }
          },
          singleLine = true,
          label = { Text(text = "New Basket Name") }
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (textFieldError) 0.dp else 4.dp),
          horizontalArrangement = Arrangement.End,
        ) {
          TextButton(onClick = {
            showEditDialog.value = false
          }) {
            Text(text = "Never mind")
          }
          TextButton(
            onClick = {
              if (renameText == "" || renameText.length > 16) {
                textFieldError = true
              } else {
                semesterBaskets[currentBasketIndex].name = renameText
                scope.launch {
                  refreshBaskets(
                    currentSemester
                  )
                }
                showEditDialog.value = false
              }
            },
            enabled = (renameText != "" && renameText.length <= 16)
          ) {
            Text(text = "Rename")
          }
        }
      }
      SideEffect {
        textFieldFocusRequester.requestFocus()
      }
    }
  }
}