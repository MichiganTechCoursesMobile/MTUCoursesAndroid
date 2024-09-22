@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.util.Log
import com.mtucoursesmobile.michigantechcourses.classes.CourseFailDrop
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface RetroFitDrop {
  @Headers(
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json"
  )
  @GET("passfaildrop")
  fun getMTUFailDrop(): Call<HashMap<String, List<CourseFailDrop>>>
}

fun getMTUCourseDropRates(failList: MutableMap<String, List<CourseFailDrop>>) {
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retroFit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

  val retrofitAPI = retroFit.create(RetroFitDrop::class.java)

  val failDropCall: Call<HashMap<String, List<CourseFailDrop>>> = retrofitAPI.getMTUFailDrop()

  failDropCall!!.enqueue(object : Callback<HashMap<String, List<CourseFailDrop>>?> {
    override fun onResponse(
      call: Call<HashMap<String, List<CourseFailDrop>>?>,
      response: Response<HashMap<String, List<CourseFailDrop>>?>
    ) {
      val failDropHash: HashMap<String, List<CourseFailDrop>> = response.body()!!
      if (failDropHash.isEmpty()) {
        return
      }
      failList.clear()
      failList.putAll(failDropHash)
      return
    }

    override fun onFailure(
      call: Call<HashMap<String, List<CourseFailDrop>>?>,
      t: Throwable
    ) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
      return
    }

  })


}
