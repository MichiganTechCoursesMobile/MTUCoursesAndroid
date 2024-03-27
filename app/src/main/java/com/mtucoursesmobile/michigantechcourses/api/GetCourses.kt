package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
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
import java.time.Instant

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
  courseList: MutableList<MTUCoursesEntry>, courseNotFound: MutableState<Boolean>, ctx: Context,
  semester: String, year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>
) {
  val okHttpClient = OkHttpClient.Builder()
    .build()

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
                if (entry.deletedAt == null) {
                  val courseBundle = MTUCourseSectionBundle(
                    mutableListOf(entry),
                    sectionData.filter { section -> section.courseId == entry.id && section.deletedAt == null }
                      .toMutableList()
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
              }
              if (courseList.isEmpty()) {
                courseNotFound.value = true
              }

              GlobalScope.launch(Dispatchers.Default) {
                lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year })
                lastUpdatedSince.add(
                  LastUpdatedSince(
                    semester,
                    year,
                    "course",
                    timeGot
                  )
                )
                lastUpdatedSince.add(
                  LastUpdatedSince(
                    semester,
                    year,
                    "section",
                    timeGot
                  )
                )
              }
            }
          }

          override fun onFailure(call: Call<ArrayList<MTUSections>?>, t: Throwable) {
            Log.d(
              "DEBUG",
              t.cause.toString()
            );
            courseNotFound.value = true
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
      courseNotFound.value = true
    }
  })
}