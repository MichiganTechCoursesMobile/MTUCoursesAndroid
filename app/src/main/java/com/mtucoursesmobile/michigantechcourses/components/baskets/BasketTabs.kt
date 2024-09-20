package com.mtucoursesmobile.michigantechcourses.components.baskets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class
)
@Composable
fun BasketTabs(
  basketViewModel: BasketViewModel,
  courseViewModel: MTUCoursesViewModel,
  db: BasketDB
) {
  val haptics = LocalHapticFeedback.current
  val showDeleteDialog = remember { mutableStateOf(false) }
  val showEditDialog = remember { mutableStateOf(false) }
  SecondaryScrollableTabRow(
    divider = { /* Remove default divider */ },
    modifier = Modifier.fillMaxWidth(),
    selectedTabIndex = basketViewModel.currentBasketIndex,
  ) {
    basketViewModel.basketList.forEachIndexed { index, item ->
      var expandedDropdown by remember { mutableStateOf(false) }
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .combinedClickable(onClick = {
            basketViewModel.setCurrentBasket(index)
          },
            onLongClick = {
              expandedDropdown = true
              haptics.performHapticFeedback(HapticFeedbackType.LongPress)
              basketViewModel.setCurrentBasket(index)
            })
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = item.name,
          textAlign = TextAlign.Center,
          modifier = Modifier
        )
        Box(modifier = Modifier.offset(y = 20.dp)) {
          DropdownMenu(
            expanded = expandedDropdown,
            onDismissRequest = { expandedDropdown = false }) {
            DropdownMenuItem(
              text = { Text(text = "Rename") },
              onClick = {
                expandedDropdown = false
                showEditDialog.value = true
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.Edit,
                  contentDescription = "Rename Basket"
                )
              }
            )
            DropdownMenuItem(
              text = { Text(text = "Duplicate") },
              onClick = {
                basketViewModel.duplicateBasket(
                  courseViewModel.currentSemester,
                  db,
                  index
                )
                expandedDropdown = false
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.CopyAll,
                  contentDescription = "Duplicate Basket"
                )
              }
            )
            DropdownMenuItem(
              text = { Text(text = "Delete") },
              onClick = {
                expandedDropdown = false
                showDeleteDialog.value = true
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.Delete,
                  contentDescription = "Delete Basket"
                )
              },
              enabled = basketViewModel.basketList.size > 1
            )
          }
        }

      }
    }
    Box {
      IconButton(
        onClick = {
          basketViewModel.addBasket(
            courseViewModel.currentSemester,
            "Basket ${basketViewModel.basketList.size + 1}",
            db
          )
        }
      ) {
        Icon(
          modifier = Modifier,
          imageVector = Icons.Outlined.PostAdd,
          contentDescription = "Add new Basket"
        )
      }
    }

  }
  when {
    showDeleteDialog.value -> {
      DeleteBasketDialog(
        showDialog = showDeleteDialog,
        currentBasketName = basketViewModel.basketList[basketViewModel.currentBasketIndex].name,
        basketViewModel = basketViewModel,
        semester = courseViewModel.currentSemester,
        currentBasketId = basketViewModel.basketList[basketViewModel.currentBasketIndex].id,
        db = db
      )
    }

    showEditDialog.value -> {
      EditBasketDialog(
        showEditDialog = showEditDialog,
        semesterBaskets = basketViewModel.basketList,
        basketViewModel = basketViewModel,
        currentSemester = courseViewModel.currentSemester,
        db = db
      )
    }
  }
  // Replace default tab divider
  HorizontalDivider(
    thickness = 1.dp,
    modifier = Modifier.fillMaxWidth()
  )
}