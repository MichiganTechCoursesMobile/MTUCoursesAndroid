package com.mtucoursesmobile.michigantechcourses.localStorage

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mtucoursesmobile.michigantechcourses.classes.CalendarEntry
import com.mtucoursesmobile.michigantechcourses.classes.CourseBasket


@ProvidedTypeConverter
class BasketConverters {
  @TypeConverter
  fun fromBasket(value: List<CourseBasket>): String = Gson().toJson(value)

  @TypeConverter
  fun toBasket(value: String): List<CourseBasket> = Gson().fromJson(
    value,
    object : TypeToken<List<CourseBasket>?>() {}.type
  )

  @TypeConverter
  fun fromPair(value: Pair<String, String>): String = Gson().toJson(value)

  @TypeConverter
  fun toPair(value: String): Pair<String, String> = Gson().fromJson(
    value,
    object : TypeToken<Pair<String, String>?>() {}.type
  )

  @TypeConverter
  fun fromCalendarEntry(
    value: MutableMap<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>>
  ): String = Gson().toJson(value)

  @TypeConverter
  fun toCalendarEntry(
    value: String
  ): MutableMap<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>> =
    Gson().fromJson(
      value,
      object :
        TypeToken<MutableMap<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>>?>() {}.type
    )

}

@Database(
  entities = [CourseBasketBundle::class, CalendarBundle::class],
  version = 1
)
@TypeConverters(BasketConverters::class)
abstract class BasketDB : RoomDatabase() {
  abstract fun basketDao(): BasketDao
}

@Entity(
  tableName = "baskets",
)
data class CourseBasketBundle(
  @PrimaryKey
  val semester: Pair<String, String>,
  val baskets: List<CourseBasket>
)

@Entity(
  tableName = "calendars",
)
data class CalendarBundle(
  @PrimaryKey
  val semester: Pair<String, String>,
  val calendars: MutableMap<String, MutableMap<String, MutableMap<Int, MutableList<CalendarEntry>>>>
)

@Dao
interface BasketDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSemesterBaskets(data: CourseBasketBundle)

  @Query("SELECT * FROM baskets WHERE semester LIKE :semester")
  fun getSemesterBaskets(semester: Pair<String, String>): CourseBasketBundle?

  @Query("DELETE FROM baskets WHERE semester LIKE :semester")
  fun deleteSemesterBaskets(semester: Pair<String, String>)

  @Query("SELECT * FROM calendars WHERE semester LIKE :semester")
  fun getCalendars(semester: Pair<String, String>): CalendarBundle?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCalendars(data: CalendarBundle)

  @Query("DELETE FROM calendars WHERE semester LIKE :semester")
  fun deleteCalendars(semester: Pair<String, String>)
}
