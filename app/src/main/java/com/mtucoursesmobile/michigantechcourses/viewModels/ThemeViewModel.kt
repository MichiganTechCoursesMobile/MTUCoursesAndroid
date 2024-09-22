package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.localStorage.ThemeType
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(userPreferences: UserPreferences) : ViewModel() {
  // Observe the DataStore flow for dynamic theme preference
  val isDynamic: StateFlow<Boolean> =
    userPreferences.isDynamicThemeFlow.map { it }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = true
    )

  // Observe the DataStore flow for theme type preference
  val isDarkTheme: StateFlow<ThemeType> =
    userPreferences.themeTypeFlow.map { it }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ThemeType.SYSTEM
    )
}