package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.localStorage.CalendarBundle
import com.mtucoursesmobile.michigantechcourses.localStorage.CourseBasketBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class BasketViewModel : ViewModel() {
  val basketList = mutableStateListOf<CourseBasket>()
  val calendarEntries =
    mutableMapOf<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>>()
  var currentBasketIndex by mutableIntStateOf(0)
  var currentBasketItems = mutableStateMapOf<String, MTUSections>()

  fun getSemesterBaskets(
    semester: CurrentSemester,
    db: BasketDB
  ) {
    val dao = db.basketDao()
    CoroutineScope(Dispatchers.IO).launch {
      var baskets = dao.getSemesterBaskets(
        Pair(
          semester.semester,
          semester.year
        )
      )
      val calendars = dao.getCalendars(
        Pair(
          semester.semester,
          semester.year
        )
      )
      // No baskets for semester exist. Create a new one
      if (baskets == null || baskets.baskets.isEmpty()) {
        baskets = CourseBasketBundle(
          Pair(
            semester.semester,
            semester.year
          ),
          mutableListOf(
            CourseBasket(
              UUID.randomUUID().toString(),
              "Basket 1",
              mutableMapOf<String, MTUSections>()
            )
          )
        )
        // Update DB
        viewModelScope.launch {
          updateBaskets(
            semester,
            db,
            baskets.baskets
          )
        }
      }
      calendarEntries.clear()
      calendars?.let {
        calendarEntries.putAll(it.calendars)
      }
      basketList.clear()
      basketList.addAll(baskets.baskets)
      setCurrentBasket(0)
    }
  }

  fun addToBasket(
    section: MTUSections,
    semester: CurrentSemester,
    db: BasketDB
  ) {
    currentBasketItems[section.id] = section
    basketList[currentBasketIndex].sections[section.id] = section
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }

    // Cleans up all the calender BS for later and adds it to the CalenderEntries
    viewModelScope.launch {
      val currentBasketID = basketList[currentBasketIndex].id
      for (day in section.time.rrules[0].config.byDayOfWeek) {
        val startHour = section.time.rrules[0].config.start.hour.toInt()
        val endHour = section.time.rrules[0].config.end.hour.toInt()
        val startMinute = section.time.rrules[0].config.start.minute.toInt()
        val endMinute = section.time.rrules[0].config.end.minute.toInt()
        val startDate = LocalDate.of(
          section.time.rrules[0].config.start.year.toInt(),
          section.time.rrules[0].config.start.month.toInt(),
          section.time.rrules[0].config.start.day.toInt()
        )
        val endDate = LocalDate.of(
          section.time.rrules[0].config.end.year.toInt(),
          section.time.rrules[0].config.end.month.toInt(),
          section.time.rrules[0].config.end.day.toInt()
        )
        calendarEntries
          .getOrPut(currentBasketID) { mutableMapOf() }
          .getOrPut(day) { mutableMapOf() }
          .getOrPut(startHour) { mutableListOf() }.add(
            CalendarEntry(
              day,
              startHour,
              endHour,
              startMinute,
              endMinute,
              startDate,
              endDate,
              section
            )
          )
      }
    }
  }

  fun removeFromBasket(
    section: MTUSections,
    semester: CurrentSemester,
    db: BasketDB,
    snackbarHostState: SnackbarHostState?
  ) {
    currentBasketItems.remove(section.id)
    basketList[currentBasketIndex].sections.remove(section.id)

    // Remove from CalendarEntries
    val currentBasketID = basketList[currentBasketIndex].id
    for (day in section.time.rrules[0].config.byDayOfWeek) {
      val startHour = section.time.rrules[0].config.start.hour.toInt()
      calendarEntries[currentBasketID]?.get(day)?.get(startHour)?.removeAll {
        it.section.id == section.id
      }
    }
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
    if (snackbarHostState != null) {
      viewModelScope.launch {
        val result = snackbarHostState
          .showSnackbar(
            message = "Section deleted from Basket",
            actionLabel = "Undo",
            duration = SnackbarDuration.Short
          )
        when (result) {
          SnackbarResult.ActionPerformed -> {
            addToBasket(
              section,
              semester,
              db
            )
          }

          SnackbarResult.Dismissed -> {
            /* Nothing happens */
          }
        }
      }
    }
  }

  fun setCurrentBasket(index: Int) {
    currentBasketIndex = index
    currentBasketItems.clear()
    currentBasketItems.putAll(basketList[currentBasketIndex].sections)
  }

  fun addBasket(
    semester: CurrentSemester,
    name: String,
    db: BasketDB
  ) {
    basketList.add(
      CourseBasket(
        UUID.randomUUID().toString(),
        name,
        mutableMapOf<String, MTUSections>()
      )
    )
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  fun removeBasket(
    semester: CurrentSemester,
    id: String,
    db: BasketDB
  ) {
    basketList.removeAll(basketList.filter { basket -> basket.id == id })
    calendarEntries.remove(id)
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  private fun updateBaskets(
    semester: CurrentSemester,
    db: BasketDB,
    baskets: List<CourseBasket>
  ) {
    val dao = db.basketDao()
    CoroutineScope(Dispatchers.IO).launch {
      dao.insertSemesterBaskets(
        CourseBasketBundle(
          Pair(
            semester.semester,
            semester.year
          ),
          baskets,
        )
      )
      dao.insertCalendars(
        CalendarBundle(
          Pair(
            semester.semester,
            semester.year
          ),
          calendarEntries
        )
      )
    }
  }

  fun duplicateBasket(
    semester: CurrentSemester,
    db: BasketDB,
    index: Int
  ) {
    viewModelScope.launch {
      val copiedSections = mutableMapOf<String, MTUSections>()
      val newID = UUID.randomUUID().toString()
      copiedSections.putAll(basketList[index].sections)
      basketList.add(
        CourseBasket(
          newID,
          "${basketList[currentBasketIndex].name} (Copy)",
          copiedSections
        )
      )
      val copiedCalendar = calendarEntries[basketList[index].id]
      if (copiedCalendar != null) {
        calendarEntries[newID] = copiedCalendar
      }
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  fun refreshBaskets(
    semester: CurrentSemester,
    db: BasketDB
  ) {
    updateBaskets(
      semester,
      db,
      basketList
    )
  }
}