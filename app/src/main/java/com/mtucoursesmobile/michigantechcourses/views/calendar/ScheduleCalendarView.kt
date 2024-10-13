package com.mtucoursesmobile.michigantechcourses.views.calendar

import CalendarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseViewModel
import java.time.LocalDate


@Composable
fun ScheduleCalendar(
  weekState: WeekCalendarState, courseViewModel: CourseViewModel,
  basketViewModel: BasketViewModel,
  activeDate: MutableState<LocalDate>,
  visibleDate: MutableState<LocalDate>
) {
  val today = remember { mutableStateOf(LocalDate.now()) }
  WeekCalendar(
    state = weekState,
    dayContent = { day ->
      val currentDayOfWeek = day.date.dayOfWeek.toString().substring(
        0,
        2
      )
      var calendarEntries: MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>? = null


      // Set Active Date and update calendarEntries safely
      basketViewModel.basketList.isNotEmpty().let {
        calendarEntries =
          basketViewModel.calendarEntries.isNotEmpty().let {
            basketViewModel.calendarEntries[basketViewModel.basketList[basketViewModel.currentBasketIndex].id]
          }

        calendarEntries?.let { entries ->
          entries.values.flatMap { it.values }.flatten().minByOrNull {
            LocalDate.of(
              it.startYear,
              it.startMonth,
              it.startDay
            )
          }.let {
            it?.let { entry ->
              LocalDate.of(
                entry.startYear,
                entry.startMonth,
                entry.startDay
              )
            }
          }
        }?.let {
          activeDate.value = it
          if (activeDate.value < today.value) {
            visibleDate.value = today.value
          } else {
            visibleDate.value = it
          }
        } ?: run {
          if (courseViewModel.currentSemester.year.toInt() != today.value.year) {
            activeDate.value = LocalDate.of(
              courseViewModel.currentSemester.year.toInt(),
              1,
              1
            )
            visibleDate.value = activeDate.value
          } else {
            activeDate.value = today.value
            visibleDate.value = today.value
          }
        }
      }
      CalendarItem(
        day.date,
        calendarEntries?.get(currentDayOfWeek),
        courseViewModel.courseList
      )
    }
  )
}





