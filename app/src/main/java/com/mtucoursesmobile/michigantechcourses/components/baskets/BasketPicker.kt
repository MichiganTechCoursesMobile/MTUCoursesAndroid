package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import kotlinx.coroutines.launch

@Composable
fun BasketPicker(
  expanded: MutableState<Boolean>,
  basketViewModel: BasketViewModel
) {
  val scope = rememberCoroutineScope()
  AnimatedVisibility(basketViewModel.basketList.size > 1) {
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
      basketViewModel.basketList.forEachIndexed { index, item ->
        DropdownMenuItem(
          text = { Text(item.name) },
          onClick = {
            expanded.value = false
            if (basketViewModel.currentBasketIndex != index) {
              scope.launch {
                basketViewModel.setCurrentBasket(index)
              }
            }

          },
          trailingIcon = {
            if (basketViewModel.currentBasketIndex == index) {
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