package com.mtucoursesmobile.michigantechcourses.views.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.mtucoursesmobile.michigantechcourses.components.baskets.BasketPicker
import com.mtucoursesmobile.michigantechcourses.components.calendar.CalendarTimes
import com.mtucoursesmobile.michigantechcourses.components.courses.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.localStorage.FirstDayOfWeek
import com.mtucoursesmobile.michigantechcourses.utils.getWeekPageTitle
import com.mtucoursesmobile.michigantechcourses.utils.rememberFirstVisibleWeekAfterScroll
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsModelProvider
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun CalendarView(
  courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel
) {
  // Load settings for first day of the Week
  val settingsModel: SettingsViewModel = viewModel(factory = SettingsModelProvider.Factory)
  val firstDayOfWeek = when (settingsModel.firstDayOfWeek.collectAsState().value) {
    FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
    FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
    FirstDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
  }
  val activeDate = remember { mutableStateOf(LocalDate.now().plusYears(50)) }
  val visibleDate = remember { mutableStateOf(LocalDate.now().plusYears(50)) }
  val calendarState = rememberWeekCalendarState(
    startDate = activeDate.value,
    endDate = activeDate.value.plusWeeks(20),
    firstVisibleWeekDate = visibleDate.value,
    firstDayOfWeek = firstDayOfWeek
  )
  val context = LocalContext.current
  val visibleWeek = rememberFirstVisibleWeekAfterScroll(calendarState)
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        title = {
          Text(text = getWeekPageTitle(visibleWeek))
        },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
          SemesterPicker(
            expanded = remember { mutableStateOf(false) },
            currentSemester = courseViewModel.currentSemester,
            semesterList = courseViewModel.semesterList,
            updateSemesterPeriod = courseViewModel::updateSemesterPeriod,
            updateSemesterYear = courseViewModel::updateSemesterYear,
            getSemesterBaskets = basketViewModel::getSemesterBaskets
          )
          BasketPicker(
            expanded = remember { mutableStateOf(false) },
            basketList = basketViewModel.basketList,
            currentBasketIndex = basketViewModel.currentBasketIndex,
            setCurrentBasket = basketViewModel::setCurrentBasket,
          )
        }
      )
    }
  ) { innerPadding ->
    Row(Modifier.padding(innerPadding)) {
      CalendarTimes()
      ScheduleCalendar(
        weekState = calendarState,
        courseViewModel = courseViewModel,
        basketViewModel = basketViewModel,
        activeDate = activeDate,
        visibleDate = visibleDate
      )
    }
  }
}
