@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
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

  @GET("instructors")
  fun getUpdatedMTUInstructors(
    @Query("updatedSince") lastUpdated: String
  ): Call<List<MTUInstructor>>
}

@OptIn(
  ExperimentalMaterial3Api::class,
)
fun updateMTUCourses(
  courseList: MutableMap<String, MTUCourses>,
  sectionList: MutableMap<String, MutableList<MTUSections>>,
  instructorList: MutableMap<Number, MTUInstructor>,
  semester: String, year: String,
  lastUpdatedSince: MutableList<LastUpdatedSince>,
  loading: MutableState<Boolean>?,
  ctx: Context,
  currentSemester: CurrentSemester
) {
  var lastUpdatedCourses =
    lastUpdatedSince.find { entry -> entry.type == "course" && entry.semester == semester && entry.year == year }?.time
  if (lastUpdatedCourses == null) {
    lastUpdatedCourses = Instant.now().toString()
  }
  var lastUpdatedSections =
    lastUpdatedSince.find { entry -> entry.type == "section" && entry.semester == semester && entry.year == year }?.time
  if (lastUpdatedSections == null) {
    lastUpdatedSections = Instant.now().toString()
  }
  var lastUpdatedInstructors =
    lastUpdatedSince.find { entry -> entry.type == "instructor" }?.time
  if (lastUpdatedInstructors == null) {
    lastUpdatedInstructors = Instant.now().toString()
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

  val instructorUpdateCall: Call<List<MTUInstructor>> =
    retrofitAPI.getUpdatedMTUInstructors(lastUpdatedInstructors)

  courseUpdateCall!!.enqueue(object : Callback<List<MTUCourses>?> {
    override fun onResponse(
      call: Call<List<MTUCourses>?>, response: Response<List<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        val updatedCourseData: List<MTUCourses> = response.body()!!
        if (currentSemester.semester == semester && currentSemester.year == year) {
          courseList.putAll(updatedCourseData.associateBy { it.id })
          lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "course" })
          lastUpdatedSince.add(
            LastUpdatedSince(
              semester,
              year,
              "course",
              Instant.now().toString()
            )
          )
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
              loading.value = false
            }
            return
          }
          sectionList.putAll(updatedSectionData.groupBy { it.courseId }.mapValuesTo(
            HashMap()
          ) { it -> it.value.map { it }.toMutableList() })
          lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.semester == semester && entry.year == year && entry.type == "section" })
          lastUpdatedSince.add(
            LastUpdatedSince(
              semester,
              year,
              "section",
              Instant.now().toString()
            )
          )
          loading?.value = false
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

  instructorUpdateCall!!.enqueue(object : Callback<List<MTUInstructor>?> {
    override fun onResponse(
      call: Call<List<MTUInstructor>?>, response: Response<List<MTUInstructor>?>
    ) {
      if (response.isSuccessful) {
        val updatedInstructorData: List<MTUInstructor> = response.body()!!
        instructorList.putAll(updatedInstructorData.associateBy { it.id })
        lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.type == "instructor" })
        lastUpdatedSince.add(
          LastUpdatedSince(
            "all",
            "all",
            "instructor",
            Instant.now().toString()
          )
        )
        return
      }
    }

    override fun onFailure(call: Call<List<MTUInstructor>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
      return
    }
  })
}