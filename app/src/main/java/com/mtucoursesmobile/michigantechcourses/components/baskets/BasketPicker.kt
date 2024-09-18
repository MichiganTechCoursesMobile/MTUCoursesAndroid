package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel

@Composable
fun BasketPicker(
  expanded: MutableState<Boolean>,
  basketViewModel: BasketViewModel
) {
  IconButton(onClick = { expanded.value = true }) {
    Icon(
      imageVector = Icons.Outlined.ShoppingBasket,
      contentDescription = "Change Basket",
      tint = MaterialTheme.colorScheme.primary,
    )
  }
  DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
    basketViewModel.basketList.forEachIndexed { index, item ->
      DropdownMenuItem(text = { Text(item.name) }, onClick = {
        basketViewModel.setCurrentBasket(index)
        expanded.value = false
      })
    }
  }
}