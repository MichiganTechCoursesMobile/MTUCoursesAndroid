package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import com.mtucoursesmobile.michigantechcourses.classes.MTUBuilding
import com.mtucoursesmobile.michigantechcourses.classes.MTUInstructor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetroFitBuildings {
  @GET("buildings")
  fun getMTUBuildings(
  ): Call<List<MTUBuilding>>

}

fun getMTUBuildings(buildingList: MutableMap<String, MTUBuilding>) {
  val okHttpClient = OkHttpClient.Builder()
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
        buildingList.clear()
        buildingList.putAll(buildingData.associateBy { it.name })
      }
    }

    override fun onFailure(call: Call<List<MTUBuilding>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      )
    }

  })
}
