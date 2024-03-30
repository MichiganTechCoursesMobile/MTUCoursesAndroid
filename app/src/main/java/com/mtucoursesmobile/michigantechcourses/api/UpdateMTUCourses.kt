package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant

interface RetroFitUpdate {
  @GET("courses")
  fun getUpdatedMTUCourses(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<List<MTUCourses>>

  @GET("sections")
  fun getUpdatedMTUSections(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<List<MTUSections>>
}

@OptIn(
  ExperimentalMaterial3Api::class,
)
fun updateMTUCourses(
  courseList: MutableMap<String, MTUCourses>,
  sectionList: MutableMap<String, MutableList<MTUSections>>,
  semester: String, year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>,
  loading: PullToRefreshState?,
  ctx: Context,
  currentSemester: CurrentSemester
) {
  var lastUpdatedCourses =
    lastUpdatedSince.find { entry -> entry.type == "course" && entry.semester == semester && entry.year == year }?.time
  if (lastUpdatedCourses == null) {
    lastUpdatedCourses = Instant.now().toString()
  }
  var lastUpdatedSections =
    lastUpdatedSince.find { entry -> entry.type == "course" && entry.semester == semester && entry.year == year }?.time
  if (lastUpdatedSections == null) {
    lastUpdatedSections = Instant.now().toString()
  }
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retroFit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

  val retrofitAPI = retroFit.create(RetroFitUpdate::class.java)

  val courseUpdateCall: Call<List<MTUCourses>> = retrofitAPI.getUpdatedMTUCourses(
    semester,
    year,
    lastUpdatedCourses
  )

  val sectionUpdateCall: Call<List<MTUSections>> = retrofitAPI.getUpdatedMTUSections(
    semester,
    year,
    lastUpdatedSections
  )

  val courseNotUpdated = false
  val sectionsNotUpdated = false

  courseUpdateCall!!.enqueue(object : Callback<List<MTUCourses>?> {
    override fun onResponse(
      call: Call<List<MTUCourses>?>, response: Response<List<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        val updatedCourseData: List<MTUCourses> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          courseList.putAll(updatedCourseData.associateBy { it.id })
        }
        return
      }
    }

    override fun onFailure(call: Call<List<MTUCourses>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
      return
    }
  })

  sectionUpdateCall!!.enqueue(object : Callback<List<MTUSections>?> {
    override fun onResponse(
      call: Call<List<MTUSections>?>, response: Response<List<MTUSections>?>
    ) {
      if (response.isSuccessful) {
        val updatedSectionData: List<MTUSections> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          if (updatedSectionData.isEmpty()) {
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
          sectionList.putAll(updatedSectionData.groupBy { it.courseId }.mapValuesTo(
            HashMap()
          ) { it -> it.value.map { it }.toMutableList() })
          loading?.endRefresh()
        }
        return
      }
    }

    override fun onFailure(call: Call<List<MTUSections>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
      return
    }

  })
}