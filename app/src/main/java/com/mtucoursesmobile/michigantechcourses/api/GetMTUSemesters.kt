@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.MTUSemesters
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit

interface RetroFitAPISemesters {
  @GET("semesters")
  fun getCurrentSemesters(
  ): Call<List<MTUSemesters>>
}

fun getMTUSemesters(
  semesterList: MutableList<MTUSemesters>,
  semesterStatus: MutableIntState,
  context: Context
) {
  val okHttpClient = OkHttpClient.Builder()
    .cache(
      Cache(
        File(
          context.cacheDir,
          "http-cache"
        ),
        maxSize = 50L * 1024L * 1024L // 50 MiB
      )
    )
    .addNetworkInterceptor(BasicCacheInterceptor())
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

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPISemesters::class.java)

  val semesterCall: Call<List<MTUSemesters>> = retrofitAPI.getCurrentSemesters()

  semesterCall!!.enqueue(object : Callback<List<MTUSemesters>?> {
    override fun onResponse(
      call: Call<List<MTUSemesters>?>,
      response: Response<List<MTUSemesters>?>
    ) {
      if (response.isSuccessful) {
        val semesterData: List<MTUSemesters> = response.body()!!

        if (semesterData.isEmpty()) {
          semesterStatus.intValue = 2
          return
        }

        semesterList.addAll(semesterData)
        semesterStatus.intValue = 1
      }
      return
    }

    override fun onFailure(
      call: Call<List<MTUSemesters>?>,
      t: Throwable
    ) {
      semesterStatus.intValue = 2
      return
    }
  })
}