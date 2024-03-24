package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesEntry
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
  val findCourse: List<MTUCoursesEntry> = courseDao.getSpecificCourseEntry(
    semester,
    year
  )

  // If course already in DB, return from local storage, otherwise, continue.
  if (findCourse.isNotEmpty()) {
    Log.d(
      "SQL DEBUG",
      "Course already in local DB, using that instead of API call."
    )
    courseList.clear()
    courseList.addAll(findCourse)
    return
  }

  Log.d(
    "SQL DEBUG",
    "Course not found in local DB. Calling API for data."
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

        var courseData: ArrayList<MTUCourses> = response.body()!!

        sectionCall!!.enqueue(object : Callback<ArrayList<MTUSections>?> {
          override fun onResponse(
            call: Call<ArrayList<MTUSections>?>, response: Response<ArrayList<MTUSections>?>
          ) {
            if (response.isSuccessful) {
              var sectionData: ArrayList<MTUSections> = response.body()!!

              if (courseList.size != 0) {
                courseList.clear()
              }

              for (entry in courseData) {
                var courseBundle = MTUCourseSectionBundle(
                  entry,
                  sectionData.filter { section -> section.courseId == entry.id })
                courseList.add(
                  MTUCoursesEntry(
                    semester,
                    year,
                    courseBundle.course.id,
                    courseBundle
                  )
                )
              }
              if (courseList.isEmpty()) {
                throw NoSuchElementException()
              }

              GlobalScope.launch(Dispatchers.Default) {
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
              "Failed to get data..",
              Toast.LENGTH_SHORT
            ).show()
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
        "Failed to get data..",
        Toast.LENGTH_SHORT
      ).show()

    }
  })
}