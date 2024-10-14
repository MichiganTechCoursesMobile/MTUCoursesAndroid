package com.mtucoursesmobile.michigantechcourses.views.courses

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.components.courses.ExpandableSearchView
import com.mtucoursesmobile.michigantechcourses.components.courses.FilterModal
import com.mtucoursesmobile.michigantechcourses.components.courses.LoadingScreen
import com.mtucoursesmobile.michigantechcourses.components.courses.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalEncodingApi::class,
)
@Composable
fun CourseView(
  courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel,
  navController: NavController,
  listState: LazyListState,
  viewSettings: DrawerState,
  basketImportContent: String? = null
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  if (basketImportContent != null && !courseViewModel.attemptedBasketImport.value) {
    val invalidBasket = {
      Log.e(
        "BasketView",
        "Invalid basket import content: $basketImportContent"
      )
      Toast.makeText(
        context,
        "Invalid basket import content",
        Toast.LENGTH_SHORT
      ).show()
      courseViewModel.attemptedBasketImport.value = true
    }
//    courseViewModel.attemptedBasketImport.value = true
    if (!basketImportContent.startsWith("MTUANDROID:")
    ) {
      try {
        if (!Base64.decode(basketImportContent)
            .decodeToString().startsWith("MTUANDROID:")
        ) {
          invalidBasket()
          return
        }
      } catch (e: Exception) {
        invalidBasket()
        return
      }
    } else {

    }
    val basketData = mutableMapOf<String, String>()
    val requestBody = try {
      Base64.decode(basketImportContent).decodeToString().substring(11)
    } catch (e: Exception) {
      basketImportContent.substring(11)
    }
    requestBody.split("&").forEach { pair ->
      val (key, value) = pair.split("=")
      basketData[key] = value
    }
    if (basketData["SEMESTER"] == null ||
      basketData["CRNS"] == null ||
      basketData["BASKET_NAME"] == null
    ) {
      Log.d(
        "DEBUG",
        "Invalid due to missing values"
      )
      invalidBasket()
      return
    }
    if (basketData["SEMESTER"]?.startsWith("SPRING") == false &&
      basketData["SEMESTER"]?.startsWith("SUMMER") == false &&
      basketData["SEMESTER"]?.startsWith("FALL") == false
    ) {
      Log.d(
        "DEBUG",
        "Invalid due to wrong semester ${basketData["SEMESTER"]}"
      )
      invalidBasket()
      return
    }
    val sharerName = if (basketData["NAME"] == "") null else basketData["NAME"]
    val (semester, year) = basketData["SEMESTER"]!!.split("-")
    val crns = basketData["CRNS"]!!.split(",")
    val basketName = basketData["BASKET_NAME"]!!

    var initialDialogIsOpen by remember { mutableStateOf(true) }
    var secondaryDialogIsOpen by remember { mutableStateOf(false) }
    when {
      initialDialogIsOpen -> {
        AlertDialog(
          title = { Text(text = "Import $basketName?") },
          text = {
            Text(
              text = "${sharerName ?: "Someone"} shared their ${
                semester.substring(
                  0,
                  1
                ) + semester.substring(1).toLowerCase(locale = Locale.current)
              } $year basket with you! Would you like to start importing it?"
            )
          },
          onDismissRequest = {
            initialDialogIsOpen = false
            courseViewModel.attemptedBasketImport.value = true
          },
          confirmButton = {
            TextButton(onClick = {
              initialDialogIsOpen = false
              secondaryDialogIsOpen = true
            }) {
              Text(text = "Yes")
            }
          },
          dismissButton = {
            TextButton(onClick = {
              initialDialogIsOpen = false
              courseViewModel.attemptedBasketImport.value = true
            }) {
              Text(text = "No")
            }
          }
        )
      }
    }
    when {
      secondaryDialogIsOpen -> {
        val newSemester = remember {
          CurrentSemester(
            "${
              semester.lowercase().replaceFirstChar(Char::titlecase)
            } $year",
            year,
            semester
          )
        }
        LaunchedEffect(Unit) {
          courseViewModel.setSemester(newSemester)
        }
        AlertDialog(
          title = {
            Text(
              text = "${
                if (courseViewModel.courseStatus.intValue != 1 ||
                  courseViewModel.sectionStatus.intValue != 1 ||
                  courseViewModel.semesterStatus.intValue != 1 ||
                  courseViewModel.instructorStatus.intValue != 1 ||
                  courseViewModel.buildingStatus.intValue != 1 ||
                  courseViewModel.dropStatus.intValue != 1
                ) "Loading" else "Confirm Import of "
              } $basketName${
                if (courseViewModel.courseStatus.intValue != 1 ||
                  courseViewModel.sectionStatus.intValue != 1 ||
                  courseViewModel.semesterStatus.intValue != 1 ||
                  courseViewModel.instructorStatus.intValue != 1 ||
                  courseViewModel.buildingStatus.intValue != 1 ||
                  courseViewModel.dropStatus.intValue != 1
                ) "..." else ""
              }"
            )
          },
          text = {
            Text(
              text = if (courseViewModel.courseStatus.intValue != 1 ||
                courseViewModel.sectionStatus.intValue != 1 ||
                courseViewModel.semesterStatus.intValue != 1 ||
                courseViewModel.instructorStatus.intValue != 1 ||
                courseViewModel.buildingStatus.intValue != 1 ||
                courseViewModel.dropStatus.intValue != 1
              ) "Please wait while the basket information is being loaded..." else "$basketName has ${crns.size} sections in it. Would you like to import it?"
            )
          },
          onDismissRequest = {
            secondaryDialogIsOpen = false
            courseViewModel.attemptedBasketImport.value = true
          },
          confirmButton = {
            TextButton(
              onClick = {
                basketViewModel.importBasket(
                  courseViewModel.currentSemester,
                  basketName,
                  crns,
                  courseViewModel.sectionList
                )
                secondaryDialogIsOpen = false
                Toast.makeText(
                  context,
                  "Importing $basketName...",
                  Toast.LENGTH_SHORT
                ).show()
                courseViewModel.attemptedBasketImport.value = true

              },
              enabled = !(courseViewModel.courseStatus.intValue != 1 ||
                  courseViewModel.sectionStatus.intValue != 1 ||
                  courseViewModel.semesterStatus.intValue != 1 ||
                  courseViewModel.instructorStatus.intValue != 1 ||
                  courseViewModel.buildingStatus.intValue != 1 ||
                  courseViewModel.dropStatus.intValue != 1)
            ) {
              Text(text = "Confirm")
            }
          },
          dismissButton = {
            TextButton(onClick = {
              secondaryDialogIsOpen = false
              courseViewModel.attemptedBasketImport.value = true
            }) {
              Text(text = "Cancel")
            }
          }
        )
      }
    }

  }


  val expanded = remember { mutableStateOf(false) }
  val topBarColor by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
  }
  val expandedFab by remember {
    derivedStateOf { (listState.lastScrolledBackward || !listState.canScrollBackward) }
  }
  val (searching, onSearchExpandedChanged) = remember {
    mutableStateOf(false)
  }

  // Ensure that the search bar is open when re-loading page (coming back from course detail view)
  LaunchedEffect(Unit) {
    if (courseViewModel.courseSearchValue.value != "") {
      onSearchExpandedChanged(true)
    }
  }
  BackHandler(searching) {
    courseViewModel.courseSearchValue.value = ""
    onSearchExpandedChanged(false)
    scope.launch { listState.animateScrollToItem(0) }
  }

  Scaffold(
    modifier = Modifier,
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = if (topBarColor) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = if (topBarColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
          AnimatedVisibility(visible = !searching) {
            AnimatedVisibility(
              visible = (courseViewModel.courseStatus.intValue == 1 &&
                  courseViewModel.sectionStatus.intValue == 1 &&
                  courseViewModel.semesterStatus.intValue == 1 &&
                  courseViewModel.instructorStatus.intValue == 1 &&
                  courseViewModel.buildingStatus.intValue == 1 &&
                  courseViewModel.dropStatus.intValue == 1),
              enter = scaleIn(
                animationSpec = tween(
                  delayMillis = 700
                )
              ),
              exit = scaleOut(
                animationSpec = tween(
                  delayMillis = 700
                )
              )
            ) {
              IconButton(onClick = { onSearchExpandedChanged(true) }) {
                Icon(
                  imageVector = Icons.Outlined.Search,
                  contentDescription = "Search Courses",
                  tint = if (topBarColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
            }
          }
          AnimatedVisibility(visible = !searching) {
            SemesterPicker(
              expanded = expanded,
              currentSemester = courseViewModel.currentSemester,
              semesterList = courseViewModel.semesterList,
              updateSemesterPeriod = courseViewModel::updateSemesterPeriod,
              updateSemesterYear = courseViewModel::updateSemesterYear,
              getSemesterBaskets = basketViewModel::getSemesterBaskets,
              topBarColor = topBarColor
            )
          }
          AnimatedVisibility(visible = !searching) {
            IconButton(onClick = {
              scope.launch {
                viewSettings.open()
              }
            }) {
              Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Open Settings",
                tint = if (topBarColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
              )
            }
          }
        },
        title = {
          if (!searching) {
            Text(text = "Courses for ${courseViewModel.currentSemester.readable}")
          }
          ExpandableSearchView(
            searchDisplay = courseViewModel.courseSearchValue.value,
            onSearchDisplayChanged = { courseViewModel.courseSearchValue.value = it },
            onSearchDisplayClosed = {
              onSearchExpandedChanged(false)
              courseViewModel.courseSearchValue.value = ""
              scope.launch { listState.animateScrollToItem(0) }
            },
            expanded = searching,
            onExpandedChanged = onSearchExpandedChanged
          )
        }
      )
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = (courseViewModel.courseStatus.intValue == 1 &&
            courseViewModel.sectionStatus.intValue == 1 &&
            courseViewModel.semesterStatus.intValue == 1 &&
            courseViewModel.instructorStatus.intValue == 1 &&
            courseViewModel.buildingStatus.intValue == 1 &&
            courseViewModel.dropStatus.intValue == 1),
        enter = scaleIn(
          animationSpec = tween(
            delayMillis = 700
          )
        ),
        exit = scaleOut(
          animationSpec = tween(
            delayMillis = 700
          )
        ),
      ) {
        FloatingActionButton(
          onClick = {
            courseViewModel.showFilter.value = true
          }
        ) {
          AnimatedContent(
            targetState = expandedFab,
            label = "Filter"
          ) { expanded ->
            if (expanded) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Filled.FilterList,
                  "Filter Button",
                  modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                  text = "Filter",
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(end = 4.dp)
                )
              }
            } else {
              Icon(
                Icons.Filled.FilterList,
                "Filter Button"
              )
            }
          }

        }
      }
    }) { innerPadding ->
    AnimatedContent(
      targetState = (
          courseViewModel.courseStatus.intValue != 1 ||
              courseViewModel.sectionStatus.intValue != 1 ||
              courseViewModel.semesterStatus.intValue != 1 ||
              courseViewModel.instructorStatus.intValue != 1 ||
              courseViewModel.buildingStatus.intValue != 1 ||
              courseViewModel.dropStatus.intValue != 1
          ),
      label = "CourseList",
    ) { isLoading ->
      if (isLoading) {
        LoadingScreen(
          innerPadding,
          courseViewModel.courseStatus.intValue,
          courseViewModel.sectionStatus.intValue,
          courseViewModel.semesterStatus.intValue,
          courseViewModel.instructorStatus.intValue,
          courseViewModel.buildingStatus.intValue,
          courseViewModel.dropStatus.intValue,
          courseViewModel::retry
        )
      } else {
        LazyCourseList(
          listState = listState,
          courseViewModel,
          navController,
          innerPadding
        )
      }
    }
    FilterModal(
      listState = listState,
      sortingMode = courseViewModel.sortingMode,
      sortingTypes = courseViewModel.sortingTypes,
      courseLevelFilter = courseViewModel.courseLevelFilter,
      courseCreditFilter = courseViewModel.courseCreditFilter,
      otherCourseFilters = courseViewModel.otherCourseFilters,
      toggleLevel = courseViewModel::toggleLevel,
      toggleCredit = courseViewModel::toggleCredit,
      toggleOther = courseViewModel::toggleOther,
      showFilter = courseViewModel.showFilter
    )
  }
}