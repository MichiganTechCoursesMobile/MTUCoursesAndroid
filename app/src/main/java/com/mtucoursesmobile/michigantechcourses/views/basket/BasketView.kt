package com.mtucoursesmobile.michigantechcourses.views.basket

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.SectionInstructors
import com.mtucoursesmobile.michigantechcourses.components.baskets.BasketItem
import com.mtucoursesmobile.michigantechcourses.components.baskets.BasketTabs
import com.mtucoursesmobile.michigantechcourses.components.courses.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun BasketView(
  courseViewModel: MTUCoursesViewModel,
  basketViewModel: BasketViewModel,
  db: BasketDB,
  navController: NavController,
  courseNavController: NavController
) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val snackbarHostState = remember { SnackbarHostState() }
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(title = { Text(text = "Baskets for ${semesterText.value}") },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
          IconButton(
            onClick = {
              Toast.makeText(
                context,
                "To Share ${basketViewModel.basketList[basketViewModel.currentBasketIndex].name}",
                Toast.LENGTH_SHORT
              ).show()
            },
            enabled = false
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "Share current Basket"
            )
          }
          SemesterPicker(
            expanded = expanded,
            currentSemester = courseViewModel.currentSemester,
            semesterList = courseViewModel.semesterList,
            updateSemesterPeriod = courseViewModel::updateSemesterPeriod,
            updateSemesterYear = courseViewModel::updateSemesterYear,
            getSemesterBaskets = basketViewModel::getSemesterBaskets,
            db = db,
            context = context,
            semesterText = semesterText,
            courseNavController = courseNavController
          )
        })
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      BasketTabs(
        basketList = basketViewModel.basketList,
        setCurrentBasket = basketViewModel::setCurrentBasket,
        duplicateBasket = basketViewModel::duplicateBasket,
        addBasket = basketViewModel::addBasket,
        removeBasket = basketViewModel::removeBasket,
        refreshBaskets = basketViewModel::refreshBaskets,
        currentBasketIndex = basketViewModel.currentBasketIndex,
        courseViewModel = courseViewModel,
        db = db
      )
      LazyColumn {
        itemsIndexed(
          items = basketViewModel.currentBasketItems.toList(),
          key = { _, section -> section.second.id }) { _, section ->
          val course = courseViewModel.courseList[section.second.courseId]
          val sectionInstructor =
            courseViewModel.instructorList.filter { instructor ->
              section.second.instructors.contains(
                SectionInstructors(instructor.key)
              )
            }
          Box(
            modifier = Modifier.animateItem()
          ) {
            BasketItem(
              section = section.second,
              course = course,
              removeFromBasket = basketViewModel::removeFromBasket,
              currentSemester = courseViewModel.currentSemester,
              db = db,
              navController = navController,
              courseNavController = courseNavController,
              instructors = sectionInstructor,
              buildings = courseViewModel.buildingList,
              snackbarHostState = snackbarHostState
            )
          }

        }
      }

    }
  }

}

