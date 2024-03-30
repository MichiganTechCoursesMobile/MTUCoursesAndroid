package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

interface RetroFitSection {

  @GET("sections")
  fun getMTUSections(
    @Query("semester") semester: String, @Query("year") year: String
  ): Call<List<MTUSections>>

}

fun getMTUSections(
  sectionList: MutableMap<String, MutableList<MTUSections>>,
  semester: String, year: String, lastUpdatedSince: MutableList<LastUpdatedSince>, context: Context,
  currentSemester: CurrentSemester
) {
  val okHttpClient = OkHttpClient.Builder()
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
      call: Call<List<MTUSections>?>, response: Response<List<MTUSections>?>
    ) {
      if (response.isSuccessful) {
        val timeGot = Instant.now().toString()
        val sectionData: List<MTUSections> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          if (sectionData.isEmpty()) {
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
        }
        return
      }
    }

    override fun onFailure(call: Call<List<MTUSections>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
      Toast.makeText(
        context,
        "Something went wrong, please try again...",
        Toast.LENGTH_LONG
      ).show()
      return
    }
  })

}