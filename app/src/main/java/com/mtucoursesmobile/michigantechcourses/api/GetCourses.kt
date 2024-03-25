package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesEntryDate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.sql.Time
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

// Define RetroFit API interface
interface RetroFitAPI {
  //Specific call for courses (year and semester)
  @GET("courses")
  fun getCourseData(
    @Query("semester") semester: String, @Query("year") year: String
  ): Call<ArrayList<MTUCourses>>

  @GET("sections")
  fun getSectionData(
    @Query("semester") semester: String, @Query("year") year: String
  ): Call<ArrayList<MTUSections>>
}

@OptIn(DelicateCoroutinesApi::class)
fun getSemesterCourses(
  courseList: MutableList<MTUCoursesEntry>, ctx: Context, semester: String, year: String,
  db: AppDatabase
) {
  // 100 MB of Cache for API GET requests
  val cacheSize = 100 * 1024 * 1024
  val cache = Cache(
    ctx.cacheDir,
    cacheSize.toLong()
  )
  val okHttpClient = OkHttpClient.Builder()
    .cache(cache)
    .build()

  // Initialize the DB courseDAO
  val courseDao = db.courseDao()

  // Look for the current semester + year in local storage
  val findCourse: List<MTUCoursesEntry> = courseDao.getSemesterEntries(
    semester,
    year
  )
  val currentDateAndTime = Instant.now()

  Log.d(
    "DEBUG",
    currentDateAndTime.toString()
  )

  // Initiate API Call via retrofit
  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPI::class.java)

  // Define the course call
  val courseCall: Call<ArrayList<MTUCourses>> = retrofitAPI.getCourseData(
    semester,
    year
  )

  val sectionCall: Call<ArrayList<MTUSections>> = retrofitAPI.getSectionData(
    semester,
    year
  )
  // Get Data from API
  courseCall!!.enqueue(object : Callback<ArrayList<MTUCourses>?> {
    override fun onResponse(
      call: Call<ArrayList<MTUCourses>?>,
      response: Response<ArrayList<MTUCourses>?>
    ) {
      if (response.isSuccessful) {

        val courseData: ArrayList<MTUCourses> = response.body()!!

        sectionCall!!.enqueue(object : Callback<ArrayList<MTUSections>?> {
          override fun onResponse(
            call: Call<ArrayList<MTUSections>?>, response: Response<ArrayList<MTUSections>?>
          ) {
            if (response.isSuccessful) {
              val sectionData: ArrayList<MTUSections> = response.body()!!
              val timeGot = Instant.now().toString()

              if (courseList.size != 0) {
                courseList.clear()
              }

              for (entry in courseData) {
                val courseBundle = MTUCourseSectionBundle(
                  mutableListOf(entry),
                  sectionData.filter { section -> section.courseId == entry.id }.toMutableList()
                )
                courseList.add(
                  MTUCoursesEntry(
                    semester,
                    year,
                    courseBundle.course[0].id,
                    courseBundle
                  )
                )
              }
              if (courseList.isEmpty()) {
                throw NoSuchElementException()
              }

              GlobalScope.launch(Dispatchers.Default) {
                courseDao.insertDateEntry(
                  MTUCoursesEntryDate(
                    semester,
                    year,
                    timeGot
                  )
                )
                for (i in courseList) {
                  courseDao.insertCourseEntry(
                    i
                  )
                }
              }
            }
          }

          override fun onFailure(call: Call<ArrayList<MTUSections>?>, t: Throwable) {
            Log.d(
              "DEBUG",
              t.cause.toString()
            );
            Toast.makeText(
              ctx,
              "Failed to get data attempting to use local DB..",
              Toast.LENGTH_SHORT
            ).show()
            if (findCourse.isNotEmpty()) {
              Log.d(
                "SQL DEBUG",
                "Course already in local DB, using that instead of API call."
              )
              courseList.clear()
              courseList.addAll(findCourse)
              return
            } else {
              Toast.makeText(
                ctx,
                "Local DB empty. Please make sure you have a stable internet connection and try again",
                Toast.LENGTH_LONG
              ).show()
            }
          }
        })
        return
      }
    }

    // If error occurs
    override fun onFailure(call: Call<ArrayList<MTUCourses>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      );
      Toast.makeText(
        ctx,
        "Failed to get data attempting to use local DB...",
        Toast.LENGTH_SHORT
      ).show()
      if (findCourse.isNotEmpty()) {
        Log.d(
          "SQL DEBUG",
          "Course already in local DB, using that instead of API call."
        )
        courseList.clear()
        courseList.addAll(findCourse)
        return
      } else {
        Toast.makeText(
          ctx,
          "Local DB not initiated, please check internet connection and try again...",
          Toast.LENGTH_LONG
        ).show()
      }
    }
  })
}