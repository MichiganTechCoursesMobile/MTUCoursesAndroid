package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSemesters
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetroFitAPISemesters {
  @GET("semesters")
  fun getCurrentSemesters(
  ): Call<ArrayList<MTUSemesters>>
}

fun getSemesters(semesterList: MutableList<CurrentSemester>, context: Context) {
  Log.d(
    "DEBUG",
    "Started Semester Call"
  )
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPISemesters::class.java)

  val semesterCall: Call<ArrayList<MTUSemesters>> = retrofitAPI.getCurrentSemesters()

  semesterCall!!.enqueue(object : Callback<ArrayList<MTUSemesters>?> {
    override fun onResponse(
      call: Call<ArrayList<MTUSemesters>?>, response: Response<ArrayList<MTUSemesters>?>
    ) {
      if (response.isSuccessful) {
        val semesterData: ArrayList<MTUSemesters> = response.body()!!

        for (semester in semesterData) {
          semesterList.add(
            CurrentSemester(
              "${semester.semester.lowercase().replaceFirstChar(Char::titlecase)} ${semester.year}",
              semester.year.toString(),
              semester.semester
            )
          )
          Log.d(
            "DEBUG",
            "Added $semester"
          )
        }
      }
    }

    override fun onFailure(call: Call<ArrayList<MTUSemesters>?>, t: Throwable) {
      Toast.makeText(context, "API not available, check Network Connection", Toast.LENGTH_LONG).show()
    }
  })
}