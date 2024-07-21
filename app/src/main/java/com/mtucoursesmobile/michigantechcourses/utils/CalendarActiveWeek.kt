package com.mtucoursesmobile.michigantechcourses.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.filter
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): Week {
  val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
  LaunchedEffect(state) {
    snapshotFlow { state.isScrollInProgress }
      .filter { scrolling -> !scrolling }
      .collect { visibleWeek.value = state.firstVisibleWeek }
  }
  return visibleWeek.value
}

fun getWeekPageTitle(week: Week): String {
  val firstDate = week.days.first().date
  val lastDate = week.days.last().date
  return when {
    firstDate.yearMonth == lastDate.yearMonth -> {
      firstDate.yearMonth.displayText()
    }

    firstDate.year == lastDate.year -> {
      "${firstDate.month.displayText(short = false)} - ${lastDate.yearMonth.displayText()}"
    }

    else -> {
      "${firstDate.yearMonth.displayText()} - ${lastDate.yearMonth.displayText()}"
    }
  }
}

fun YearMonth.displayText(short: Boolean = false): String {
  return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
  val style = if (short) TextStyle.SHORT else TextStyle.FULL
  return getDisplayName(
    style,
    Locale.ENGLISH
  )
}