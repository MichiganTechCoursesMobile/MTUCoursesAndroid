package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester

@Composable
fun DeleteBasketDialog(
  showDialog: MutableState<Boolean>, currentBasketName: String,
  semester: CurrentSemester, currentBasketId: String,
  removeBasket: (CurrentSemester, String) -> Unit,
  currentBasketIndex: Int, setCurrentBasket: (Int) -> Unit,
) {
  AlertDialog(
    title = { Text(text = "Delete Basket?") },
    text = { Text(text = "Are you sure you want to delete $currentBasketName? This action cannot be undone.") },
    onDismissRequest = { showDialog.value = false },
    confirmButton = {
      TextButton(onClick = {
        showDialog.value = false
        removeBasket(
          semester,
          currentBasketId
        )
        if (currentBasketIndex - 1 >= 0) {
          setCurrentBasket(currentBasketIndex - 1)
        } else {
          setCurrentBasket(currentBasketIndex)
        }

      }) {
        Text(
          text = "Delete",
          color = MaterialTheme.colorScheme.error
        )
      }
    },
    dismissButton = {
      TextButton(onClick = { showDialog.value = false }) {
        Text(text = "Never mind")
      }
    }
  )
}