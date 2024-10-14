@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit

interface RetroFitCourses {
  @Headers(
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json"
  )
  @GET("courses")
  fun getMTUCourses(
    @Query("semester") semester: String,
    @Query("year") year: String
  ): Call<List<MTUCourses>>
}

fun getMTUCourses(
  courseList: MutableMap<String, MTUCourses>,
  semester: String,
  year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>,
  currentSemester: CurrentSemester,
  courseStatus: MutableIntState,
  context: Context
) {
  val okHttpClient = OkHttpClient.Builder()
    .cache(
      Cache(
        File(
          context.cacheDir,
          "http-cache"
        ),
        maxSize = 50L * 1024L * 1024L // 50 MiB
      )
    )
    .addNetworkInterceptor(CourseCacheInterceptor())
    .readTimeout(
      15,
      TimeUnit.SECONDS
    )
    .connectTimeout(
      5,
      TimeUnit.SECONDS
    )
    .callTimeout(
      25,
      TimeUnit.SECONDS
    )
    .build()

  val retroFit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

  val retrofitAPI = retroFit.create(RetroFitCourses::class.java)

  val courseCall: Call<List<MTUCourses>> = retrofitAPI.getMTUCourses(
    semester,
    year
  )

  courseCall!!.enqueue(object : Callback<List<MTUCourses>?> {
    override fun onResponse(
      call: Call<List<MTUCourses>?>,
      response: Response<List<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        val timeGot = Instant.now().toString()
        val courseData: List<MTUCourses> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          if (courseData.isEmpty()) {
            courseStatus.intValue = 2
            return
          }
          courseList.clear()
          courseList.putAll(courseData.associateBy { it.id })
          lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "course" })
          lastUpdatedSince.add(
            LastUpdatedSince(
              semester,
              year,
              "course",
              timeGot
            )
          )
          courseStatus.intValue = 1
        }
        return
      }
    }

    override fun onFailure(
      call: Call<List<MTUCourses>?>,
      t: Throwable
    ) {
      courseStatus.intValue = 2
      return
    }
  })

}