@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
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

interface RetroFitBuildings {
  @GET("buildings")
  fun getMTUBuildings(
  ): Call<List<MTUBuilding>>

}

fun getMTUBuildings(
  buildingList: MutableMap<String, MTUBuilding>, buildingStatus: MutableIntState, context: Context
) {
  val okHttpClient = OkHttpClient.Builder()
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
    .cache(
      Cache(
        File(
          context.cacheDir,
          "buildingCache"
        ),
        maxSize = 50L * 1024L * 1024L // 50 MiB
      )
    )
    .addNetworkInterceptor(BasicCacheInterceptor())
    .build()

  val retroFit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

  val retrofitAPI = retroFit.create(RetroFitBuildings::class.java)

  val buildingCall: Call<List<MTUBuilding>> = retrofitAPI.getMTUBuildings()

  buildingCall!!.enqueue(object : Callback<List<MTUBuilding>?> {
    override fun onResponse(
      call: Call<List<MTUBuilding>?>, response: Response<List<MTUBuilding>?>
    ) {
      if (response.isSuccessful) {
        val buildingData: List<MTUBuilding> = response.body()!!
        if (buildingData.isEmpty()) {
          buildingStatus.intValue = 2
          return
        }
        buildingList.clear()
        buildingList.putAll(buildingData.associateBy { it.name })
        buildingStatus.intValue = 1
        return
      }
    }

    override fun onFailure(call: Call<List<MTUBuilding>?>, t: Throwable) {
      buildingStatus.intValue = 2
      return
    }

  })
}
