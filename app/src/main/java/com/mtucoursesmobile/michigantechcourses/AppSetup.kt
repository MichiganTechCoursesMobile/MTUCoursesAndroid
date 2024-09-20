package com.mtucoursesmobile.michigantechcourses

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences

val Context.dataStore by preferencesDataStore("appSettings")

class AppSetup : Application() {
  lateinit var userPreferences: UserPreferences

  override fun onCreate() {
    super.onCreate()
    userPreferences = UserPreferences(dataStore)
  }
}