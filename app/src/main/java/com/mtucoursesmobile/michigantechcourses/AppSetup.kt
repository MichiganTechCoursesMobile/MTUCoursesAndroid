package com.mtucoursesmobile.michigantechcourses

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketConverters
import com.mtucoursesmobile.michigantechcourses.localStorage.BasketDB
import com.mtucoursesmobile.michigantechcourses.localStorage.UserPreferences

val Context.dataStore by preferencesDataStore("appSettings")

class AppSetup : Application() {
  lateinit var userPreferences: UserPreferences
  lateinit var dataBase: BasketDB

  override fun onCreate() {
    super.onCreate()
    dataBase = Room.databaseBuilder(
      applicationContext,
      BasketDB::class.java,
      "basket-db"
    ).addTypeConverter(BasketConverters()).build()
    userPreferences = UserPreferences(dataStore)
  }
}