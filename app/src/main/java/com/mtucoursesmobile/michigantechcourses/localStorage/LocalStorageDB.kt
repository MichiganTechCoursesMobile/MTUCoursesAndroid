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
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses


@ProvidedTypeConverter
class MTUCoursesConverter {
  @TypeConverter
  fun StringToMTUCourses(string: String?): MTUCourseSectionBundle {
    return Gson().fromJson(
      string,
      object : TypeToken<MTUCourseSectionBundle?>() {}.type
    )
  }

  @TypeConverter
  fun MTUCoursesToString(data: MTUCourseSectionBundle): String? {
    return Gson().toJson(data)
  }
}


@Database(
  entities = [MTUCoursesEntry::class, MTUCoursesEntryDate::class],
  version = 1
)
@TypeConverters(MTUCoursesConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun courseDao(): CourseDao
}

@Entity(
  tableName = "mtucourses",
  primaryKeys = ["semester", "year", "courseId"]
)
data class MTUCoursesEntry(
  val semester: String,
  val year: String,
  val courseId: String,
  val entry: MTUCourseSectionBundle
)

@Entity(
  tableName = "mtucoursesDate",
  primaryKeys = ["semester", "year"]
)
data class MTUCoursesEntryDate(
  val semester: String,
  val year: String,
  val lastUpdated: String,
)

@Dao
interface CourseDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCourseEntry(data: MTUCoursesEntry)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertDateEntry(data: MTUCoursesEntryDate)

  @Query("SELECT * FROM mtucourses")
  fun getAllCourseEntries(): List<MTUCoursesEntry>

  @Query("SELECT * FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year")
  fun getSemesterEntries(semester: String, year: String): List<MTUCoursesEntry>

  @Query("SELECT * FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year AND courseId LIKE :courseId")
  fun getSpecificCourse(semester: String, year: String, courseId: String): MTUCoursesEntry

  @Query("SELECT * FROM mtucoursesDate WHERE semester LIKE :semester AND year LIKE :year")
  fun getSemesterDate(semester: String, year: String): MTUCoursesEntryDate

  @Query("DELETE FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year AND courseId LIKE :courseId")
  fun deleteCourseEntry(semester: String, year: String, courseId: String)
}