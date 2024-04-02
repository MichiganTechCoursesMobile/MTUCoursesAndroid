package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel

@Composable
fun DeleteCourseDialog(
  showDialog: MutableState<Boolean>, currentBasketName: String, basketViewModel: BasketViewModel,
  semester: CurrentSemester, currentBasketId: String, db: BasketDB
) {
  AlertDialog(
    title = { Text(text = "Delete Basket?") },
    text = { Text(text = "Are you sure you want to delete $currentBasketName? This action cannot be undone.") },
    onDismissRequest = { showDialog.value = false },
    confirmButton = {
      TextButton(onClick = {
        showDialog.value = false
        basketViewModel.removeBasket(
          semester,
          currentBasketId,
          db
        )
        if (basketViewModel.currentBasketIndex - 1 >= 0) {
          basketViewModel.setCurrentBasket(basketViewModel.currentBasketIndex - 1)
        }
        basketViewModel.setCurrentBasket(basketViewModel.currentBasketIndex)
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