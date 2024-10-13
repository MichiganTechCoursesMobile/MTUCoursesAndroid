package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mtucoursesmobile.michigantechcourses.localStorage.FirstDayOfWeek
import com.mtucoursesmobile.michigantechcourses.localStorage.ThemeType
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(private val userPreferences: UserPreferences) : ViewModel() {

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

  val sharingEnabled: StateFlow<Boolean> = userPreferences.sharingEnabledFlow.map { it }.stateIn(
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

  fun updateFirstDayOfWeek(firstDayOfWeek: FirstDayOfWeek) {
    userPreferences.setFirstDayOfWeek(
      firstDayOfWeek,
      viewModelScope
    )
  }

  fun updateSharingEnabled() {
    userPreferences.setSharingEnabled(
      !sharingEnabled.value,
      viewModelScope
    )
  }
}

object SettingsModelProvider {
  val Factory = viewModelFactory {
    initializer {
      ThemeViewModel(appViewModelProvider().userPreferences)
    }

    initializer {
      SettingsViewModel(appViewModelProvider().userPreferences)
    }
  }
}