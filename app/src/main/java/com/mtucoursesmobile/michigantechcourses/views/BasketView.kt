package com.mtucoursesmobile.michigantechcourses.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.components.baskets.BasketTabs
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketView(
  courseViewModel: MTUCoursesViewModel, basketViewModel: BasketViewModel, db: BasketDB
) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val semesterBaskets = remember { basketViewModel.basketList }
  val currentBasketItems = remember { basketViewModel.currentBasketItems }
  val currentSemester = remember { courseViewModel.currentSemester }
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
      BasketTabs(basketViewModel = basketViewModel, courseViewModel = courseViewModel, db = db)
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

    }
  }

}

