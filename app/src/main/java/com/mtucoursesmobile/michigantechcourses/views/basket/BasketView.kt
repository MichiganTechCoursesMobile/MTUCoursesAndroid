package com.mtucoursesmobile.michigantechcourses.views.basket

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import java.text.DecimalFormat

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun BasketView(
  courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel,
  navController: NavController,
  courseNavController: NavController
) {
  val expanded = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val semesterText = remember { mutableStateOf(courseViewModel.currentSemester.readable) }
  val snackbarHostState = remember { SnackbarHostState() }
  val listState = rememberLazyListState()
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
            semesterText = semesterText,
            courseNavController = courseNavController
          )
        })
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    floatingActionButton = {
      AnimatedVisibility(
        visible = basketViewModel.currentBasketItems.size != 0,
        enter = slideInHorizontally(
          initialOffsetX = {
            (it / 2) * 3
          },
        ),
        exit = slideOutHorizontally(
          targetOffsetX = {
            (it / 2) * 3
          },
        )
      ) {
        val maxCredits =
          basketViewModel.currentBasketItems.toList().sumOf { it.second.maxCredits.toDouble() }
        val minCredits =
          basketViewModel.currentBasketItems.toList().sumOf { it.second.minCredits.toDouble() }
        FloatingActionButton(
          onClick = {
            courseViewModel.showFilter.value = true
          }
        ) {
          AnimatedContent(
            targetState = (listState.lastScrolledBackward || !listState.canScrollBackward),
            label = "Credits"
          ) { isScrolling ->
            if (isScrolling) {
              Text(
                text = "${
                  if (maxCredits == minCredits) DecimalFormat(
                    "0.#"
                  ).format(maxCredits) else {
                    "${DecimalFormat("0.#").format(minCredits)} - ${
                      DecimalFormat(
                        "0.#"
                      ).format(maxCredits)
                    }"
                  }

                } Credit${if (maxCredits > 1 || maxCredits == 0.0) "s" else ""}",
                modifier = Modifier.padding(horizontal = 10.dp)
              )
            } else {
              Text(
                text = if (maxCredits == minCredits) DecimalFormat(
                  "0.#"
                ).format(maxCredits) else {
                  "${DecimalFormat("0.#").format(minCredits)} - ${
                    DecimalFormat(
                      "0.#"
                    ).format(maxCredits)
                  }"
                },
                modifier = Modifier.padding(horizontal = 10.dp)
              )
            }
          }

        }
      }

    }
  ) { innerPadding ->

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
      )
      LazyColumn(state = listState) {
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

