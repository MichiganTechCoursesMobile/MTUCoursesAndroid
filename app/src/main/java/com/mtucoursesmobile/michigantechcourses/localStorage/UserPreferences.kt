package com.mtucoursesmobile.michigantechcourses.localStorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val IS_DYNAMIC_THEME = "is_dynamic_theme"
const val THEME_TYPE = "theme_type"
const val FIRST_DAY_OF_WEEK = "first_day_of_week"

enum class ThemeType { SYSTEM, LIGHT, DARK }

enum class FirstDayOfWeek { SATURDAY, SUNDAY, MONDAY }


class UserPreferences(private val dataStore: DataStore<Preferences>) {
  companion object {
    val isDynamicTheme = booleanPreferencesKey(IS_DYNAMIC_THEME)
    val themeType = intPreferencesKey(THEME_TYPE)
    val firstDayOfWeek = intPreferencesKey(FIRST_DAY_OF_WEEK)
  }

  val isDynamicThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[isDynamicTheme] ?: true
  }

  val themeTypeFlow: Flow<ThemeType> = dataStore.data.map { preferences ->
    ThemeType.entries[preferences[themeType] ?: 0]
  }

  val firstDayOfWeekFlow: Flow<FirstDayOfWeek> = dataStore.data.map { preferences ->
    FirstDayOfWeek.entries[preferences[firstDayOfWeek] ?: 1]
  }

  fun setDynamicTheme(value: Boolean, scope: CoroutineScope) {
    scope.launch {
      dataStore.edit { preferences ->
        preferences[isDynamicTheme] = value
      }
    }
  }

  fun setThemeType(value: ThemeType, scope: CoroutineScope) {
    scope.launch {
      dataStore.edit { preferences ->
        preferences[themeType] = value.ordinal
      }
    }
  }

  fun setFirstDayOfWeek(value: FirstDayOfWeek, scope: CoroutineScope) {
    scope.launch {
      dataStore.edit { preferences ->
        preferences[firstDayOfWeek] = value.ordinal
      }
    }
  }

}