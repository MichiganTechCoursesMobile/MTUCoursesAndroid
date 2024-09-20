package com.mtucoursesmobile.michigantechcourses.localStorage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsHandler(private val userPreferences: UserPreferences) : ViewModel() {

  val themeType: StateFlow<ThemeType> =
    userPreferences.themeTypeFlow.map { it }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ThemeType.DARK
    )

  val isDynamic: StateFlow<Boolean> =
    userPreferences.isDynamicThemeFlow.map { it }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false
    )

  val firstDayOfWeek: StateFlow<FirstDayOfWeek> = userPreferences.firstDayOfWeekFlow.map { it }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = FirstDayOfWeek.SUNDAY
    )


  fun updateIsDynamicTheme() {
    userPreferences.setDynamicTheme(
      !isDynamic.value,
      viewModelScope
    )
  }

  fun updateThemeType(themeType: ThemeType) {
    userPreferences.setThemeType(
      themeType,
      viewModelScope
    )
  }

  fun updateFirstDayOfWeek(firstDayOfWeek: FirstDayOfWeek) {
    userPreferences.setFirstDayOfWeek(
      firstDayOfWeek,
      viewModelScope
    )
  }
}