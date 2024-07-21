package com.mtucoursesmobile.michigantechcourses.views

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.components.SemesterPicker
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.utils.toHslColor
import com.mtucoursesmobile.michigantechcourses.viewModels.BasketViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
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
fun CalendarView(
  basketViewModel: BasketViewModel, courseViewModel: MTUCoursesViewModel, db: BasketDB
) {
  val initialDate = remember { mutableStateOf(LocalDate.now().plusYears(50)) }
  val state = rememberWeekCalendarState(
    startDate = initialDate.value,
    endDate = initialDate.value.plusWeeks(20),
    firstVisibleWeekDate = initialDate.value
  )
  val context = LocalContext.current
  val visibleWeek = rememberFirstVisibleWeekAfterScroll(state)
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
          IconButton(onClick = { /*TODO*/ }) {
            Icon(
              imageVector = Icons.Outlined.ShoppingBasket,
              contentDescription = "Choose Basket"
            )
          }
          /*TODO: Add Basket picker*/
//          Button(onClick = {
//            basketViewModel.setCurrentBasket(1)
//          }) {}
        }
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
            val borderColor = MaterialTheme.colorScheme.outline
            Canvas(
              modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
            ) {
              val strokeWidth = 0.15.dp.toPx()

              drawRect(
                color = borderColor,
                style = Stroke(strokeWidth)
              )
            }
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .fillParentMaxHeight(1.toFloat() / items.size.toFloat())
                .background(Color.Transparent)
                .zIndex(2f),
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
          val currentDayOfWeek = day.date.dayOfWeek.toString().substring(
            0,
            2
          )
          if (courseViewModel.currentSemester.year.toInt() != LocalDate.now().year) {
            initialDate.value = LocalDate.of(
              courseViewModel.currentSemester.year.toInt(),
              1,
              1
            )
          } else if (basketViewModel.calendarEntries[basketViewModel.basketList[basketViewModel.currentBasketIndex].id].isNullOrEmpty()) {
            initialDate.value = LocalDate.now()
          } else {
            initialDate.value = LocalDate.now().plusYears(50)
          }
          Day(
            day.date,
            basketViewModel.calendarEntries[basketViewModel.basketList[basketViewModel.currentBasketIndex].id]?.get(currentDayOfWeek),
            courseViewModel,
            initialDate
          )
        }
      )
    }
  }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
private fun Day(
  date: LocalDate, dayEntries: MutableMap<Int, MutableList<CalendarEntry>>?,
  courseViewModel: MTUCoursesViewModel, initialDate: MutableState<LocalDate>
) {
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
          val borderColor = MaterialTheme.colorScheme.outline
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .zIndex(1f)
          ) {
            val strokeWidth = 0.15.dp.toPx()
            drawRect(
              color = borderColor,
              style = Stroke(strokeWidth)
            )
          }

          var boxHeight by remember { mutableStateOf(0.dp) }
          val localDensity = LocalDensity.current
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillParentMaxHeight(1 / items.size.toFloat())
              .onSizeChanged {
                with(localDensity) {
                  boxHeight = it.height.toDp()
                }
              }
              .zIndex(2f)
          ) {
            Row(
              Modifier
                .fillMaxSize()
            ) {
              if (dayEntries != null) {
                if (dayEntries[time]?.isNotEmpty() == true) {
                  for (entry in dayEntries[time]!!) {
                    val startDate = LocalDate.of(
                      entry.startYear,
                      entry.startMonth,
                      entry.startDay
                    )
                    val endDate = LocalDate.of(
                      entry.endYear,
                      entry.endMonth,
                      entry.endDay
                    )

                    if (startDate < initialDate.value) {
                      initialDate.value = startDate
                    }

                    if (date >= startDate && date <= endDate) {
                      val classLength = (calculateClassLength(
                        entry.startHour,
                        entry.startMinute,
                        entry.endHour,
                        entry.endMinute
                      ).toFloat() / 60.toFloat())
                      Box {
                        val cardHeight = (boxHeight * classLength)
                        if (courseViewModel.courseList[entry.section.courseId] != null) {
                          Card(
                            colors = CardDefaults.cardColors(
                              containerColor = Color(
                                "${courseViewModel.courseList[entry.section.courseId]?.title}${entry.section.id}".toHslColor(
                                  saturation = 0.6f,
                                  lightness = 0.6f
                                )
                              )
                            ),
                            modifier = Modifier
                              .fillParentMaxWidth((1.toFloat() / dayEntries[time]!!.filter {
                                val indivStartDate = LocalDate.of(
                                  it.startYear,
                                  it.startMonth,
                                  it.startDay
                                )
                                val indivEndDate = LocalDate.of(
                                  it.endYear,
                                  it.endMonth,
                                  it.endDay
                                )
                                indivStartDate <= date && indivEndDate >= date
                              }.size.toFloat()))
                              .requiredHeight(cardHeight)
                              .offset(y = if (classLength > 1f) (cardHeight - boxHeight) / 2 else 0.dp)
                              .offset(y = entry.startMinute / 60f * boxHeight),
                            onClick = {
                              Log.d(
                                "DEBUG",
                                "Clicked"
                              )
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                          ) {

                            Text(
                              text = "${courseViewModel.courseList[entry.section.courseId]?.subject} ${courseViewModel.courseList[entry.section.courseId]?.crse}",
                              fontSize = 8.sp,
                              modifier = Modifier.padding(
                                start = 4.dp,
                                top = 4.dp
                              ),
                              lineHeight = 12.sp,
                              color = MaterialTheme.colorScheme.inverseOnSurface,
                              fontWeight = FontWeight.Bold
                            )
                          }
                        }

                      }
                    }
                  }
                }
              }
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

fun calculateClassLength(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Int {
  return (endHour * 60 + endMinute) - (startHour * 60 + startMinute)
}

fun Modifier.drawBorderBehind(
  strokeWidth: Dp,
  color: Color
): Modifier = this.then(
  object : DrawModifier {
    override fun ContentDrawScope.draw() {
      // Draw the content first (including clipped parts)
      drawContent()

      // Then draw the border on top of everything
      val strokeWidthPx = strokeWidth.toPx()
      drawRect(
        color = color,
        style = Stroke(strokeWidthPx)
      )
    }
  }
)