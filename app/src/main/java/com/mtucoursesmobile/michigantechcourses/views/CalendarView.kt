package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import kotlinx.coroutines.flow.filter
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
fun CalendarView(basketViewModel: BasketViewModel) {
  val currentCalendar =
    remember { basketViewModel.calendarEntries[basketViewModel.basketList[basketViewModel.currentBasketIndex].id] }
  val currentDate = remember { LocalDate.now() }
  val state = rememberWeekCalendarState(
    startDate = currentDate,
    endDate = currentDate.plusWeeks(16),
    firstVisibleWeekDate = currentDate
  )
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
    Row(Modifier.padding(innerPadding)) {
      Column {
        Box(Modifier.padding(top = 52.dp)) {
          Box(
            modifier = Modifier
              .width(50.dp)
              .height(0.25.dp)
              .background(MaterialTheme.colorScheme.onSurface)
              .align(Alignment.BottomCenter)
          )
        }
        val items = (7..22).toList()
        LazyColumn(
          modifier = Modifier
            .width(50.dp),
          userScrollEnabled = false
        ) {
          itemsIndexed(
            items,
            key = { index, _ -> index }) { _, time ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .fillParentMaxHeight(1.toFloat() / items.size.toFloat())
                .background(Color.Transparent),
              contentAlignment = Alignment.TopEnd
            ) {
              if (time < 12) {
                Text(text = "$time am")
              } else if (time == 12) {
                Text(text = "$time pm")
              } else {
                Text(text = "${time - 12} pm")
              }
            }
          }
        }
      }
      WeekCalendar(
        state = state,
        dayContent = { day ->
          var currentDayOfWeek = ""
          when (day.date.dayOfWeek.toString()) {
            "MONDAY" -> currentDayOfWeek = "MO"
            "TUESDAY" -> currentDayOfWeek = "TU"
            "WEDNESDAY" -> currentDayOfWeek = "WE"
            "THURSDAY" -> currentDayOfWeek = "TH"
            "FRIDAY" -> currentDayOfWeek = "FR"
          }
          val dayEntries = currentCalendar?.get(currentDayOfWeek)
          Day(
            day.date,
            dayEntries
          )
        }
      )
    }
  }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
private fun Day(date: LocalDate, dayEntries: MutableMap<Int, MutableList<CalendarEntry>>?) {
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Column(
        modifier = Modifier.padding(
          bottom = 4.dp
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
      Box() {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(0.25.dp)
            .background(MaterialTheme.colorScheme.onSurface)
            .align(Alignment.BottomCenter)
        )
      }
      val items = (7..22).toList()
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth(),
        userScrollEnabled = false
      ) {
        itemsIndexed(
          items,
          key = { index, _ -> index }) { _, time ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillParentMaxHeight((1.toFloat() / items.size.toFloat()))
          ) {
            if (dayEntries != null) {
              Log.d(
                "CalendarStuff",
                "dayEntries is not null"
              )
              if (dayEntries[time] != null) {
                Box() {
                  Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                      .fillMaxWidth()
                      .fillMaxHeight()
                      .align(Alignment.TopCenter),
                    onClick = {
                      Log.d(
                        "DEBUG",
                        "Clicked"
                      )
                    }
                  ) {}
                }
              }
            } else {
              Log.d(
                "CalendarStuff",
                "dayEntries is null"
              )
            }
          }
        }
      }
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