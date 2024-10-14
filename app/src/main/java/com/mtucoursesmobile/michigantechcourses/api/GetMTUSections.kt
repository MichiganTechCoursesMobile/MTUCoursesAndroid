@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit

interface RetroFitSection {

  @GET("sections")
  fun getMTUSections(
    @Query("semester") semester: String,
    @Query("year") year: String
  ): Call<List<MTUSections>>

}

fun getMTUSections(
  sectionList: MutableMap<String, MutableList<MTUSections>>,
  semester: String,
  year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>,
  currentSemester: CurrentSemester,
  sectionStatus: MutableIntState,
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

  val retrofitAPI = retroFit.create(RetroFitSection::class.java)

  val sectionCall: Call<List<MTUSections>> = retrofitAPI.getMTUSections(
    semester,
    year
  )
  sectionList.clear()

  sectionCall!!.enqueue(object : Callback<List<MTUSections>?> {
    override fun onResponse(
      call: Call<List<MTUSections>?>,
      response: Response<List<MTUSections>?>
    ) {
      if (response.isSuccessful) {
        val timeGot = Instant.now().toString()
        val sectionData: List<MTUSections> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          if (sectionData.isEmpty()) {
            sectionStatus.intValue = 2
            return
          }
          sectionList.putAll(sectionData.groupBy { it.courseId }.mapValuesTo(
            HashMap()
          ) { it -> it.value.map { it }.toMutableList() })
          lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "section" })
          lastUpdatedSince.add(
            LastUpdatedSince(
              semester,
              year,
              "section",
              timeGot
            )
          )
          sectionStatus.intValue = 1
        }
        return
      }
    }

    override fun onFailure(
      call: Call<List<MTUSections>?>,
      t: Throwable
    ) {
      sectionStatus.intValue = 2
      return
    }
  })

}