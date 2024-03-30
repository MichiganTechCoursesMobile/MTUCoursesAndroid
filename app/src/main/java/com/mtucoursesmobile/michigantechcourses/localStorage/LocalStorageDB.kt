package com.mtucoursesmobile.michigantechcourses.localStorage


//@ProvidedTypeConverter
//class MTUCoursesConverter {
//  @TypeConverter
//  fun StringToMTUCourses(string: String?): MTUCourseSectionBundle {
//    return Gson().fromJson(
//      string,
//      object : TypeToken<MTUCourseSectionBundle?>() {}.type
//    )
//  }
//
//  @TypeConverter
//  fun MTUCoursesToString(data: MTUCourseSectionBundle): String? {
//    return Gson().toJson(data)
//  }
//}
//
//@Database(
//  entities = [MTUCoursesEntry::class],
//  version = 1
//)
//@TypeConverters(MTUCoursesConverter::class)
//abstract class AppDatabase : RoomDatabase() {
//  abstract fun courseDao(): CourseDao
//}
//
//@Entity(
//  tableName = "mtucourses",
//  primaryKeys = ["semester", "year", "courseId"]
//)
//data class MTUCoursesEntry(
//  val semester: String,
//  val year: String,
//  val courseId: String,
//  val entry: MTUCourseSectionBundle
//)
//
//@Dao
//interface CourseDao {
//  @Insert(onConflict = OnConflictStrategy.REPLACE)
//  fun insertCourseEntry(data: MTUCoursesEntry)
//
//  @Query("SELECT * FROM mtucourses")
//  fun getAllCourseEntries(): List<MTUCoursesEntry>
//
//  @Query("SELECT * FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year")
//  fun getSemesterEntries(semester: String, year: String): List<MTUCoursesEntry>
//
//  @Query("SELECT * FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year AND courseId LIKE :courseId")
//  fun getSpecificCourse(semester: String, year: String, courseId: String): MTUCoursesEntry
//
//
//  @Query("DELETE FROM mtucourses WHERE semester LIKE :semester AND year LIKE :year AND courseId LIKE :courseId")
//  fun deleteCourseEntry(semester: String, year: String, courseId: String)
//}