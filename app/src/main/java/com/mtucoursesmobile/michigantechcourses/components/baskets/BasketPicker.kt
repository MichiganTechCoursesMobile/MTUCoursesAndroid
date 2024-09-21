package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket
import kotlinx.coroutines.launch

@Composable
fun BasketPicker(
  expanded: MutableState<Boolean>,
  basketList: SnapshotStateList<CourseBasket>,
  currentBasketIndex: Int,
  setCurrentBasket: (Int) -> Unit
) {
  val scope = rememberCoroutineScope()
  AnimatedVisibility(basketList.size > 1) {
    IconButton(
      onClick = { expanded.value = true }
    ) {
      Icon(
        imageVector = Icons.Outlined.ShoppingBasket,
        contentDescription = "Change Basket",
        tint = MaterialTheme.colorScheme.primary,
      )
    }
    DropdownMenu(
      expanded = expanded.value,
      onDismissRequest = { expanded.value = false }) {
      basketList.forEachIndexed { index, item ->
        DropdownMenuItem(
          text = { Text(item.name) },
          onClick = {
            expanded.value = false
            if (currentBasketIndex != index) {
              scope.launch {
                setCurrentBasket(index)
              }
            }

          },
          trailingIcon = {
            if (currentBasketIndex == index) {
              Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Check",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        )
      }
    }
  }

}