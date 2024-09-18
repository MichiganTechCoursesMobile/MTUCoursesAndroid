package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBasket
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
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.components.calendar.CalendarTimes
import com.mtucoursesmobile.michigantechcourses.components.calendar.ScheduleCalendar
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.utils.getWeekPageTitle
import com.mtucoursesmobile.michigantechcourses.utils.rememberFirstVisibleWeekAfterScroll
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import java.time.LocalDate

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun CalendarView(
  basketViewModel: BasketViewModel,
  courseViewModel: MTUCoursesViewModel,
  db: BasketDB
) {
  val activeDate = remember { mutableStateOf(LocalDate.now().plusYears(50)) }
  val calendarState = rememberWeekCalendarState(
    startDate = activeDate.value,
    endDate = activeDate.value.plusWeeks(20),
    firstVisibleWeekDate = activeDate.value
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
            courseViewModel = courseViewModel,
            basketViewModel = basketViewModel,
            db = db,
            context = context,
            semesterText = remember { mutableStateOf("") }
          )
          IconButton(onClick = { /*TODO*/ }, enabled = false) {
            Icon(
              imageVector = Icons.Outlined.ShoppingBasket,
              contentDescription = "Choose Basket",
            )
          }
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
        activeDate = activeDate
      )
    }
  }
}
