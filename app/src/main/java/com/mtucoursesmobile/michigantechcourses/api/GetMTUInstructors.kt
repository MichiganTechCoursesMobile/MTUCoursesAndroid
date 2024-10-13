@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.LastUpdatedSince
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.Instant
import java.util.concurrent.TimeUnit

interface RetroFitInstructors {
  @GET("instructors")
  fun getMTUInstructors(
  ): Call<List<MTUInstructor>>

}

fun getMTUInstructors(
  instructorList: MutableMap<Number, MTUInstructor>,
  lastUpdatedSince: MutableList<LastUpdatedSince>, instructorStatus: MutableIntState,
  context: Context
) {
  val okHttpClient = OkHttpClient.Builder()
    .cache(
      Cache(
        context.cacheDir,
        maxSize = 50L * 1024L * 1024L // 50 MiB
      )
    )
    .readTimeout(
      10,
      TimeUnit.SECONDS
    )
    .connectTimeout(
      2,
      TimeUnit.SECONDS
    )
    .writeTimeout(
      10,
      TimeUnit.SECONDS
    )
    .callTimeout(
      10,
      TimeUnit.SECONDS
    )
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
        if (instructorData.isEmpty()) {
          instructorStatus.intValue = 2
          return
        }
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
        instructorStatus.intValue = 1
      }
      return
    }

    override fun onFailure(call: Call<List<MTUInstructor>?>, t: Throwable) {
      instructorStatus.intValue = 2
      return
    }

  })
}
