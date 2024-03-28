package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import com.mtucoursesmobile.michigantechcourses.classes.MTUSemesters
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetroFitAPIInstructors {
  @GET("instructors")
  fun getCurrentInstructors(
  ): Call<List<MTUInstructor>>
}

fun getInstructors(instructorList: MutableList<MTUInstructor>, context: Context) {
  Log.d(
    "DEBUG",
    "Started Semester Call"
  )
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPIInstructors::class.java)

  val semesterCall: Call<List<MTUInstructor>> = retrofitAPI.getCurrentInstructors()

  semesterCall!!.enqueue(object : Callback<List<MTUInstructor>?> {
    override fun onResponse(
      call: Call<List<MTUInstructor>?>, response: Response<List<MTUInstructor>?>
    ) {
      if (response.isSuccessful) {
        val instructorData: List<MTUInstructor> = response.body()!!
        instructorList.clear()
        instructorList.addAll(instructorData)
      }
    }

    override fun onFailure(call: Call<List<MTUInstructor>?>, t: Throwable) {
      Toast.makeText(
        context,
        "API not available, check Network Connection",
        Toast.LENGTH_LONG
      ).show()
    }
  })
}