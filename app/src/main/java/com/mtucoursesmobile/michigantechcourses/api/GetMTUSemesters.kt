package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
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
  ): Call<List<MTUSemesters>>
}

fun getMTUSemesters(semesterList: MutableList<CurrentSemester>, context: Context) {
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPISemesters::class.java)

  val semesterCall: Call<List<MTUSemesters>> = retrofitAPI.getCurrentSemesters()

  semesterCall!!.enqueue(object : Callback<List<MTUSemesters>?> {
    override fun onResponse(
      call: Call<List<MTUSemesters>?>, response: Response<List<MTUSemesters>?>
    ) {
      if (response.isSuccessful) {
        val semesterData: List<MTUSemesters> = response.body()!!

        for (semester in semesterData) {
          semesterList.add(
            CurrentSemester(
              "${semester.semester.lowercase().replaceFirstChar(Char::titlecase)} ${semester.year}",
              semester.year.toString(),
              semester.semester
            )
          )
        }
      }
    }

    override fun onFailure(call: Call<List<MTUSemesters>?>, t: Throwable) {
      Toast.makeText(
        context,
        "API not available, check Network Connection",
        Toast.LENGTH_LONG
      ).show()
    }
  })
}