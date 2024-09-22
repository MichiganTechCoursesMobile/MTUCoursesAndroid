package com.mtucoursesmobile.michigantechcourses.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.AppSetup
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.classes.SectionTimeRRulesConfig
import com.mtucoursesmobile.michigantechcourses.localStorage.CalendarBundle
import com.mtucoursesmobile.michigantechcourses.localStorage.CourseBasketBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Year
import java.util.Calendar
import java.util.UUID

class BasketViewModel(app: Application) :
  AndroidViewModel(app) {
  private var db = (app as AppSetup).dataBase
  val basketList = mutableStateListOf<CourseBasket>()
  val calendarEntries =
    mutableMapOf<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>>()
  var currentBasketIndex by mutableIntStateOf(0)
  var currentBasketItems = mutableStateMapOf<String, MTUSections>()

  // Initialize View Model by getting the current semester baskets
  init {
    fun initialSemester(): CurrentSemester {
      var targetSemester = "FALL"
      var targetYear = Year.now().value.toString()
      if (Calendar.getInstance().get(Calendar.MONTH) + 1 > 8) {
        targetSemester = "SPRING"
        targetYear = (Year.now().value + 1).toString()
      }
      return CurrentSemester(
        "${
          targetSemester.lowercase().replaceFirstChar(Char::titlecase)
        } $targetYear",
        targetYear,
        targetSemester
      )
    }
    Log.d(
      "Test",
      "Testing View Model Run"
    )
    viewModelScope.launch {
      getSemesterBaskets(
        initialSemester(),
      )
    }
  }

  // Get the selected semester's baskets
  fun getSemesterBaskets(
    semester: CurrentSemester
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


  // Add a section to a basket
  fun addToBasket(
    section: MTUSections,
    semester: CurrentSemester
  ) {
    currentBasketItems[section.id] = section
    basketList[currentBasketIndex].sections[section.id] = section
    viewModelScope.launch {
      updateBaskets(
        semester,
        basketList
      )
    }

    // Cleans up all the calender BS for later and adds it to the CalenderEntries
    viewModelScope.launch {
      val currentBasketID = basketList[currentBasketIndex].id
      if (section.time.rrules.isNotEmpty()) {
        for (day in section.time.rrules[0].config.byDayOfWeek) {
          val startHour = section.time.rrules[0].config.start.hour.toInt()
          calendarEntries
            .getOrPut(currentBasketID) { mutableMapOf() }
            .getOrPut(day) { mutableMapOf() }
            .getOrPut(startHour) { mutableListOf() }.add(
              convertCalendarEntry(
                day,
                section.time.rrules[0].config,
                section
              )
            )
        }
      }
    }
  }

  // Remove a section from a basket
  fun removeFromBasket(
    section: MTUSections,
    semester: CurrentSemester,
    snackbarHostState: SnackbarHostState?
  ) {
    currentBasketItems.remove(section.id)
    basketList[currentBasketIndex].sections.remove(section.id)

    // Remove from CalendarEntries
    val currentBasketID = basketList[currentBasketIndex].id
    if (section.time.rrules.isNotEmpty()) {
      for (day in section.time.rrules[0].config.byDayOfWeek) {
        val startHour = section.time.rrules[0].config.start.hour.toInt()
        calendarEntries[currentBasketID]?.get(day)?.get(startHour)?.removeAll {
          it.section.id == section.id
        }
        if (calendarEntries[currentBasketID]?.get(day)?.get(startHour)?.isEmpty() == true) {
          calendarEntries[currentBasketID]?.get(day)?.remove(startHour)
        }
        if (calendarEntries[currentBasketID]?.get(day)?.isEmpty() == true) {
          calendarEntries[currentBasketID]?.remove(day)
        }
      }
    }
    viewModelScope.launch {
      updateBaskets(
        semester,
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
              semester
            )
          }

          SnackbarResult.Dismissed -> {
            /* Nothing happens */
          }
        }
      }
    }
  }

  // Change the current basket
  fun setCurrentBasket(index: Int) {
    currentBasketIndex = index
    currentBasketItems.clear()
    currentBasketItems.putAll(basketList[currentBasketIndex].sections)
  }

  // Create a new basket
  fun addBasket(
    semester: CurrentSemester,
    name: String
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
        basketList
      )
    }
  }

  // Delete a specific basket
  fun removeBasket(
    semester: CurrentSemester,
    id: String
  ) {
    basketList.removeAll(basketList.filter { basket -> basket.id == id })
    calendarEntries.remove(id)
    viewModelScope.launch {
      updateBaskets(
        semester,
        basketList
      )
    }
  }

  // Refresh / Update a specific basket
  private fun updateBaskets(
    semester: CurrentSemester,
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

  // Duplicate a basket
  fun duplicateBasket(
    semester: CurrentSemester,
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
        basketList
      )
    }
  }

  // Refresh all baskets for a given semester
  fun refreshBaskets(
    semester: CurrentSemester
  ) {
    updateBaskets(
      semester,
      basketList
    )
  }
  
  // Converts sectionTimeRules into a CalendarEntry for easier access in CalendarView
  private fun convertCalendarEntry(
    day: String, sectionTime: SectionTimeRRulesConfig, section: MTUSections
  ): CalendarEntry {
    return CalendarEntry(
      day,
      sectionTime.start.hour.toInt(),
      sectionTime.end.hour.toInt(),
      sectionTime.start.minute.toInt(),
      sectionTime.end.minute.toInt(),
      sectionTime.start.day.toInt(),
      sectionTime.start.month.toInt(),
      sectionTime.start.year.toInt(),
      sectionTime.end.day.toInt(),
      sectionTime.end.month.toInt(),
      sectionTime.end.year.toInt(),
      section
    )
  }
}