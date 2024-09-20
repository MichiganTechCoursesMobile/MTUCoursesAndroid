package com.mtucoursesmobile.michigantechcourses.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtucoursesmobile.michigantechcourses.localStorage.ThemeType
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeHandler(private val userPreferences: UserPreferences) : ViewModel() {

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
}