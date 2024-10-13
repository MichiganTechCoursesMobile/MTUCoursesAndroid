import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.utils.CalculateClassLength
import com.mtucoursesmobile.michigantechcourses.utils.toHslColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
fun CalendarItem(
  date: LocalDate, dayEntries: MutableMap<Int, MutableList<CalendarEntry>>?,
  courseList: SnapshotStateMap<String, MTUCourses>
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
    // Day Header
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
      Box {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(0.25.dp)
            .background(MaterialTheme.colorScheme.onSurface)
            .align(Alignment.BottomCenter)
        )
      }

      // Day Schedule
      val times = (7..22).toList() // 7AM - 10PM
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth(),
        userScrollEnabled = false
      ) {
        itemsIndexed(
          times,
          key = { index, _ -> index }) { _, time ->
          val borderColor = MaterialTheme.colorScheme.outline
          // Border
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
          // Idek, this was so long ago. Good luck figuring it out.
          var boxHeight by remember { mutableStateOf(0.dp) } // Calculate Box Height Dynamically
          val localDensity = LocalDensity.current
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillParentMaxHeight(1 / times.size.toFloat())
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
              dayEntries?.let { entries ->
                entries[time]?.forEach { entry ->
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

                  if (date in startDate..endDate) {
                    val classLength = (CalculateClassLength(
                      entry.startHour,
                      entry.startMinute,
                      entry.endHour,
                      entry.endMinute
                    ).toFloat() / 60.toFloat())
                    Box {
                      val cardHeight = (boxHeight * classLength)
                      courseList[entry.section.courseId]?.let {
                        Card(
                          colors = CardDefaults.cardColors(
                            containerColor = Color(
                              "${courseList[entry.section.courseId]?.title}${entry.section.id}".toHslColor(
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
                              date in indivStartDate..indivEndDate
                            }.size.toFloat()))
                            .requiredHeight(cardHeight)
                            .offset(y = if (classLength > 1f) (cardHeight - boxHeight) / 2 else 0.dp)
                            .offset(y = entry.startMinute / 60f * boxHeight),
                          onClick = {
                          },
                          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {

                          Text(
                            text = "${courseList[entry.section.courseId]?.subject} ${courseList[entry.section.courseId]?.crse}",
                            fontSize = 8.sp,
                            modifier = Modifier.padding(
                              start = 6.dp,
                              top = 6.dp
                            ),
                            lineHeight = 12.sp,
                            color = Color.Black,
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