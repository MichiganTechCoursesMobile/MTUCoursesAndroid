package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB

@Composable
fun DeleteBasketDialog(
  showDialog: MutableState<Boolean>, currentBasketName: String,
  semester: CurrentSemester, currentBasketId: String, db: BasketDB,
  removeBasket: (CurrentSemester, String, BasketDB) -> Unit,
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
          currentBasketId,
          db
        )
        if (currentBasketIndex - 1 >= 0) {
          setCurrentBasket(currentBasketIndex - 1)
        } else {
          setCurrentBasket(currentBasketIndex)
        }

      }) {
        Text(text = "Delete")
      }
    },
    dismissButton = {
      TextButton(onClick = { showDialog.value = false }) {
        Text(text = "Never mind")
      }
    }
  )
}