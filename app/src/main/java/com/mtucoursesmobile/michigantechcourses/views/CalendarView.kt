package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(
  ExperimentalMaterial3Api::class
)
@Composable
fun CalendarView() {
  val currentDate = remember { LocalDate.now() }
  val state = rememberWeekCalendarState(
    startDate = currentDate,
    endDate = currentDate.plusWeeks(16),
    firstVisibleWeekDate = currentDate
  )
  var lastSeenWeek by remember { mutableStateOf(state.lastVisibleWeek) }
  val visibleWeek = rememberFirstVisibleWeekAfterScroll(state)
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        title = {
          Text(text = "Calendar " + getWeekPageTitle(visibleWeek))
        },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  ) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      WeekCalendar(
        state = state,
        dayContent = { day ->
          Day(
            day.date
          )
        }
      )
      AnimatedContent(
        targetState = lastSeenWeek,
        label = "selectedDate",
        transitionSpec = {
          if (initialState.days.first().date < targetState.days.first().date) {
            (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
          } else {
            (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(slideOutHorizontally { height -> height } + fadeOut())
          }
        }
      ) { date ->
        Log.d(
          "DEBUG",
          date.toString()
        )
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
        ) {
          Text(text = lastSeenWeek.toString())
          if (state.lastVisibleWeek == state.firstVisibleWeek) {
            lastSeenWeek = state.lastVisibleWeek
          }
        }
      }

    }


  }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
private fun Day(date: LocalDate) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(
        RoundedCornerShape(
          topStart = 8.dp,
          topEnd = 8.dp
        )
      )
      .wrapContentHeight(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.padding(
        bottom = 8.dp
      ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = date.dayOfWeek.getDisplayName(
          TextStyle.SHORT,
          Locale.US
        ),
        fontSize = 14.sp,
        color = if (date == LocalDate.now()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
      )
      Text(
        text = dateFormatter.format(date),
        fontSize = 18.sp,
        color = if (date == LocalDate.now()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
      )
    }
    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(0.25.dp)
          .background(MaterialTheme.colorScheme.onSurface)
          .align(Alignment.BottomCenter),
      )
    }
  }
}

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