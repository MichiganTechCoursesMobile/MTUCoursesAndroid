package com.mtucoursesmobile.michigantechcourses.views.courses

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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.components.baskets.BasketImporter
import com.mtucoursesmobile.michigantechcourses.components.courses.ExpandableSearchView
import com.mtucoursesmobile.michigantechcourses.components.courses.FilterModal
import com.mtucoursesmobile.michigantechcourses.components.courses.LoadingScreen
import com.mtucoursesmobile.michigantechcourses.components.courses.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsModelProvider
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
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
  val settingsModel: SettingsViewModel = viewModel(factory = SettingsModelProvider.Factory)
  val sharingEnabled by settingsModel.sharingEnabled.collectAsState()
  if (basketImportContent != null && !courseViewModel.attemptedBasketImport.value && sharingEnabled) {
    BasketImporter(
      basketImportContent = basketImportContent,
      context = context,
      attemptedBasketImport = courseViewModel.attemptedBasketImport,
      courseStatus = courseViewModel.courseStatus,
      sectionStatus = courseViewModel.sectionStatus,
      semesterStatus = courseViewModel.semesterStatus,
      instructorStatus = courseViewModel.instructorStatus,
      buildingStatus = courseViewModel.buildingStatus,
      dropStatus = courseViewModel.dropStatus,
      sectionList = courseViewModel.sectionList,
      currentSemester = courseViewModel.currentSemester,
      setSemester = courseViewModel::setSemester,
      importBasket = basketViewModel::importBasket,
      getSemesterBaskets = basketViewModel::getSemesterBaskets,
      basketStatus = basketViewModel.basketStatus
    )
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
              courseViewModel.courseSearchValue.value = ""
              onSearchExpandedChanged(false)
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