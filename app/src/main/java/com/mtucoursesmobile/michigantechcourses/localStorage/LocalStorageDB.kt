package com.mtucoursesmobile.michigantechcourses.localStorage

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses


@ProvidedTypeConverter
class MTUCoursesConverter {
  @TypeConverter
  fun StringToMTUCourses(string: String?): MutableList<MTUCourses>? {
    return Gson().fromJson(
      string,
      object : TypeToken<MutableList<MTUCourses?>?>() {}.type
    )
  }

  @TypeConverter
  fun MTUCoursesToString(data: MutableList<MTUCourses>?): String? {
    return Gson().toJson(data)
  }
}


@Database(
  entities = [MTUCoursesEntry::class],
  version = 1
)
@TypeConverters(MTUCoursesConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun courseDao(): CourseDao
}

@Entity(
  tableName = "mtucourses",
  primaryKeys = ["semester", "year"]
)
data class MTUCoursesEntry(
  val semester: String,
  val year: String,
  val entry: MutableList<MTUCourses>?
)

@Dao
interface CourseDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCourseEntry(data: MTUCoursesEntry)

  @Query("SELECT * FROM mtucourses")
  fun getAllCourseEntries(): List<MTUCoursesEntry>

  @Query("SELECT * FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year")
  fun getSpecificCourseEntry(semester: String, year: String): List<MTUCoursesEntry>
}