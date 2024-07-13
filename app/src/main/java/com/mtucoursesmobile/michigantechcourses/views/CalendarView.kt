package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalAnimationApi::class
)
@Composable
fun CalendarView() {
  val currentDate = remember { LocalDate.now() }
  var selectedDate by remember { mutableStateOf<LocalDate?>(currentDate) }
  val state = rememberWeekCalendarState(
    startDate = currentDate,
    firstVisibleWeekDate = currentDate,
    firstDayOfWeek = DayOfWeek.MONDAY
  )
  var selection by remember { mutableStateOf(currentDate) }
  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      TopAppBar(
        title = {
          Text(text = "Calendar")
        },
        colors = TopAppBarDefaults.topAppBarColors(
          titleContentColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  ) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      WeekCalendar(
        dayContent = { day ->
          Day(
            day.date,
            isSelected = selection == day.date
          ) { clicked ->
            if (selection != clicked) {
              selection = clicked
            }
            selectedDate = clicked
          }
        },
        state = state
      )
      AnimatedContent(
        targetState = selectedDate,
        label = "selectedDate",
        transitionSpec = {
          if (targetState!! > initialState) {
            (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
          } else {
            (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(slideOutHorizontally { height -> height } + fadeOut())
          }
        }
      ) { date ->
        Text(text = date.toString())
      }
    }


  }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
private fun Day(date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(
        RoundedCornerShape(
          topStart = 8.dp,
          topEnd = 8.dp
        )
      )
      .wrapContentHeight()
      .clickable { onClick(date) },
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = date.dayOfWeek.getDisplayName(
          TextStyle.SHORT,
          java.util.Locale.US
        ),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = dateFormatter.format(date),
        fontSize = 14.sp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
      )
    }
    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
      AnimatedContent(
        targetState = isSelected,
        label = "selectedDateSlider"
      ) { selected ->
        if (selected) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp)
              .background(MaterialTheme.colorScheme.primary)
              .align(Alignment.BottomCenter),
          )
        } else {
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

  }
}

fun onClick(day: WeekDay) {
  Log.d(
    "Hello",
    day.date.toString()
  )
}
