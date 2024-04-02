package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.localStorage.CourseBasketBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class BasketViewModel : ViewModel() {
  val basketList = mutableStateListOf<CourseBasket>()
  var currentBasketIndex by mutableIntStateOf(0)
  var currentBasketItems = mutableStateMapOf<String, MTUSections>()

  fun getSemesterBaskets(semester: CurrentSemester, db: BasketDB) {
    val dao = db.basketDao()
    CoroutineScope(Dispatchers.IO).launch {
      var baskets = dao.getSemesterBaskets(
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
      basketList.clear()
      basketList.addAll(baskets.baskets)
      setCurrentBasket(0)
    }

  }

  fun addToBasket(section: MTUSections, semester: CurrentSemester, db: BasketDB) {
    currentBasketItems[section.id] = section
    basketList[currentBasketIndex].sections[section.id] = section
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  fun removeFromBasket(section: MTUSections, semester: CurrentSemester, db: BasketDB) {
    currentBasketItems.remove(section.id)
    basketList[currentBasketIndex].sections.remove(section.id)
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  fun setCurrentBasket(index: Int) {
    currentBasketIndex = index
    currentBasketItems.clear()
    currentBasketItems.putAll(basketList[currentBasketIndex].sections)
  }

  fun addBasket(semester: CurrentSemester, name: String, db: BasketDB) {
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

  fun removeBasket(semester: CurrentSemester, id: String, db: BasketDB) {
    basketList.removeAll(basketList.filter { basket -> basket.id == id })
    viewModelScope.launch {
      updateBaskets(
        semester,
        db,
        basketList
      )
    }
  }

  private fun updateBaskets(
    semester: CurrentSemester, db: BasketDB, baskets: List<CourseBasket>
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
    }
  }

  fun refreshBaskets(semester: CurrentSemester, db: BasketDB) {
    updateBaskets(
      semester,
      db,
      basketList
    )
  }
}