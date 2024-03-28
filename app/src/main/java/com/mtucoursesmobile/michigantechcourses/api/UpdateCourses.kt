package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourseSectionBundle
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUCoursesEntry
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
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

interface RetroFitAPIDate {
  @GET("sections")
  fun updatedSectionData(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<List<MTUSections>>

  @GET("courses")
  fun updatedCourseData(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<List<MTUCourses>>
}

@OptIn(
  DelicateCoroutinesApi::class,
  ExperimentalMaterial3Api::class
)
fun updateSemesterCourses(
  courseList: MutableList<MTUCoursesEntry>, ctx: Context, semester: String, year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>, loading: PullToRefreshState?
) {
  val lastUpdatedCourse =
    lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "course" }[0].time

  val lastUpdatedSection =
    lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "section" }[0].time

  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPIDate::class.java)

  val courseCall: Call<List<MTUCourses>> = retrofitAPI.updatedCourseData(
    semester,
    year,
    lastUpdatedCourse
  )

  val sectionCall: Call<List<MTUSections>> = retrofitAPI.updatedSectionData(
    semester,
    year,
    lastUpdatedSection
  )

  courseCall!!.enqueue(object : Callback<List<MTUCourses>?> {
    override fun onResponse(
      call: Call<List<MTUCourses>?>,
      response: Response<List<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        val courseData: List<MTUCourses> = response.body()!!
        val timeGot = Instant.now().toString()
        sectionCall!!.enqueue(object : Callback<List<MTUSections>?> {
          override fun onResponse(
            call: Call<List<MTUSections>?>, response: Response<List<MTUSections>?>
          ) {
            if (response.isSuccessful) {
              val sectionData: List<MTUSections> = response.body()!!
              if (courseData.size == 0 && sectionData.size == 0) {
                if (loading != null) {
                  Toast.makeText(
                    ctx,
                    "Nothing to update",
                    Toast.LENGTH_SHORT
                  ).show()
                  loading.endRefresh()
                }
                return
              }
              GlobalScope.launch(Dispatchers.IO) {
                if (courseData.size != 0) {
                  lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "course" })
                  lastUpdatedSince.add(
                    LastUpdatedSince(
                      semester,
                      year,
                      "course",
                      timeGot
                    )
                  )
                  // Courses
                  val newCourseList = mutableListOf<MTUCoursesEntry>()
                  for (i in courseData) {
                    val course = courseList.find { entry -> i.id == entry.courseId }
                    if (course != null) {
                      course.entry.course[0] = i
                    } else {
                      val newCourseYay = MTUCoursesEntry(
                        semester,
                        year,
                        i.id,
                        MTUCourseSectionBundle(
                          mutableListOf(i),
                          mutableListOf()
                        )
                      )
                      newCourseList.add(newCourseYay)
                    }
                  }
                  courseList.addAll(newCourseList)
                }
                if (sectionData.size != 0) {
                  lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "section" })
                  lastUpdatedSince.add(
                    LastUpdatedSince(
                      semester,
                      year,
                      "section",
                      timeGot
                    )
                  )
                  // Sections
                  for (i in sectionData) {
                    val foundCourse = courseList.filter { entry -> entry.courseId == i.courseId }
                    if (foundCourse.isNotEmpty()) {
                      foundCourse[0].entry.sections.removeIf { section -> section?.id == i.id }
                      foundCourse[0].entry.sections.add(i)
                    }
                  }
                }

                if (loading != null) {
                  MainScope().launch {
                    loading.endRefresh()
                    Toast.makeText(
                      ctx,
                      "Updated ${courseData.size} Courses and ${sectionData.size} Sections",
                      Toast.LENGTH_SHORT
                    ).show()
                  }
                }
              }
            }
            return
          }

          override fun onFailure(call: Call<List<MTUSections>?>, t: Throwable) {
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
      return
    }

    override fun onFailure(call: Call<List<MTUCourses>?>, t: Throwable) {
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