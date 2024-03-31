package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.Instant

interface RetroFitInstructors {
  @GET("instructors")
  fun getMTUInstructors(
  ): Call<List<MTUInstructor>>

}

fun getMTUInstructors(
  instructorList: MutableMap<Number, MTUInstructor>, context: Context,
  lastUpdatedSince: MutableList<LastUpdatedSince>
) {
  val okHttpClient = OkHttpClient.Builder()
    .build()

  val retroFit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

  val retrofitAPI = retroFit.create(RetroFitInstructors::class.java)

  val instructorCall: Call<List<MTUInstructor>> = retrofitAPI.getMTUInstructors()

  instructorCall!!.enqueue(object : Callback<List<MTUInstructor>?> {
    override fun onResponse(
      call: Call<List<MTUInstructor>?>, response: Response<List<MTUInstructor>?>
    ) {
      if (response.isSuccessful) {
        val instructorData: List<MTUInstructor> = response.body()!!
        instructorList.clear()
        instructorList.putAll(instructorData.associateBy { it.id })
        lastUpdatedSince.removeAll(lastUpdatedSince.filter { entry -> entry.type == "course" })
        lastUpdatedSince.add(
          LastUpdatedSince(
            "all",
            "all",
            "instructor",
            Instant.now().toString()
          )
        )
      }
    }

    override fun onFailure(call: Call<List<MTUInstructor>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
    }

  })
}
