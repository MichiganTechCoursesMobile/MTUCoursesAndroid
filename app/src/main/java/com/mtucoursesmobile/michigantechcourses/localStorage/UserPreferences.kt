package com.mtucoursesmobile.michigantechcourses.localStorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferences(private val dataStore: DataStore<Preferences>) {
  private companion object {
    val CURRENT_THEME = stringPreferencesKey("current_theme")
  }

  val themeMode: Flow<String> =
    dataStore.data.map { preferences ->
      preferences[CURRENT_THEME] ?: "default"
    }

  suspend fun saveTheme(currentTheme: String) {
    dataStore.edit { preferences ->
      preferences[CURRENT_THEME] = currentTheme
    }
  }
}