package com.mtucoursesmobile.michigantechcourses.views

import android.graphics.fonts.FontFamily
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mtucoursesmobile.michigantechcourses.components.DeleteCourseDialog
import com.mtucoursesmobile.michigantechcourses.components.EditBasketDialog
import com.mtucoursesmobile.michigantechcourses.components.FancyTabIndicator
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class
)
@Composable
fun BasketView(
  courseViewModel: MTUCoursesViewModel, basketViewModel: BasketViewModel, db: BasketDB
) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val haptics = LocalHapticFeedback.current
  val semesterBaskets = remember { basketViewModel.basketList }
  val currentBasketItems = remember { basketViewModel.currentBasketItems }
  val currentSemester = remember { courseViewModel.currentSemester }
  val showDeleteDialog = remember { mutableStateOf(false) }
  val showEditDialog = remember { mutableStateOf(false) }
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        title = { Text(text = "Baskets for ${semesterText.value}") },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
          IconButton(onClick = {
            Toast.makeText(
              context,
              "To Share ${semesterBaskets[basketViewModel.currentBasketIndex].name}",
              Toast.LENGTH_SHORT
            ).show()
          }) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "Share current Basket",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          SemesterPicker(
            expanded,
            courseViewModel,
            basketViewModel,
            db,
            context,
            semesterText
          )
        }
      )
    }
  ) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      SecondaryScrollableTabRow(
        divider = { /* Remove default divider */ },
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = basketViewModel.currentBasketIndex,
        indicator = @Composable {
          FancyTabIndicator(Modifier.tabIndicatorOffset(it[basketViewModel.currentBasketIndex]))
        }) {
        semesterBaskets.forEachIndexed { index, item ->
          var expandedDropdown by remember { mutableStateOf(false) }
          Box(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .padding(10.dp)
              .clip(RoundedCornerShape(2.dp))
              .combinedClickable(onClick = {
                basketViewModel.setCurrentBasket(index)
              },
                onLongClick = {
                  expandedDropdown = true
                  haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                  basketViewModel.setCurrentBasket(index)
                }),
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
                  enabled = semesterBaskets.size > 1
                )
              }
            }

          }
        }
        Box() {
          IconButton(
            onClick = {
              basketViewModel.addBasket(
                currentSemester,
                "Basket ${semesterBaskets.size + 1}",
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
      // Replace default tab divider
      HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
      )
      LazyColumn {
        itemsIndexed(
          items = currentBasketItems.toList(),
          key = { _, section -> section.second.id }) { _, section ->
          Text(text = section.second.crn)
          Button(onClick = {
            basketViewModel.removeFromBasket(
              section.second,
              currentSemester,
              db
            )
          }) {
            Text(text = "Delete section")
          }
        }
      }
      when {
        showDeleteDialog.value -> {
          DeleteCourseDialog(
            showDialog = showDeleteDialog,
            currentBasketName = semesterBaskets[basketViewModel.currentBasketIndex].name,
            basketViewModel = basketViewModel,
            semester = currentSemester,
            currentBasketId = semesterBaskets[basketViewModel.currentBasketIndex].id,
            db = db
          )
        }

        showEditDialog.value -> {
          EditBasketDialog(
            showEditDialog = showEditDialog,
            semesterBaskets = semesterBaskets,
            basketViewModel = basketViewModel,
            currentSemester = currentSemester,
            db = db
          )
        }
      }
    }
  }

}

