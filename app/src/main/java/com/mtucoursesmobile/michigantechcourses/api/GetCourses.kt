package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


data class MTUCourses(
  val id: String, val year: Int, val semester: String, val subject: String, val crse: String,
  val title: String,
  val description: String, val updatedAt: String, val deletedAt: String, val prereqs: String,
  val offered: Array<String>, val minCredits: Double, val maxCredits: Double
)

interface RetroFitAPI {
  @GET("courses")
  fun getCourseData(
    @Query("semester") semester: String, @Query("year") year: String
  ): Call<ArrayList<MTUCourses>>
}

fun getCourses(courseList: MutableList<MTUCourses>, ctx: Context, semester: String, year: String) {
  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/")
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPI::class.java)

  val call: Call<ArrayList<MTUCourses>> = retrofitAPI.getCourseData(
    semester,
    year
  )

  call!!.enqueue(object : Callback<ArrayList<MTUCourses>?> {
    override fun onResponse(
      call: Call<ArrayList<MTUCourses>?>,
      response: Response<ArrayList<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        Log.d(
          "DEBUG",
          response.body().toString()
        )
        var lst: ArrayList<MTUCourses> = ArrayList()

        lst = response.body()!!

        if (courseList.size != 0) {
          courseList.clear()
        }

        for (i in 0 until lst.size) {
          Log.d(
            "DEBUG",
            lst[i].toString()
          )
          courseList.add(lst[i])
        }
      }
    }

    override fun onFailure(call: Call<ArrayList<MTUCourses>?>, t: Throwable) {
      Toast.makeText(
        ctx,
        "Failed to get data..",
        Toast.LENGTH_SHORT
      ).show()
    }
  })
}