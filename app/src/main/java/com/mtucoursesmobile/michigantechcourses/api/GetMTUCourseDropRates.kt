@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import androidx.compose.runtime.MutableIntState
import com.mtucoursesmobile.michigantechcourses.classes.CourseFailDrop
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.io.File
import java.util.concurrent.TimeUnit

interface RetroFitDrop {
  @Headers(
    "Content-Type: application/json;charset=utf-8",
    "Accept: application/json"
  )
  @GET("passfaildrop")
  fun getMTUFailDrop(): Call<HashMap<String, List<CourseFailDrop>>>
}

fun getMTUCourseDropRates(
  failList: MutableMap<String, List<CourseFailDrop>>,
  dropStatus: MutableIntState,
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
      15,
      TimeUnit.SECONDS
    )
    .connectTimeout(
      5,
      TimeUnit.SECONDS
    )
    .callTimeout(
      25,
      TimeUnit.SECONDS
    )
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
        dropStatus.intValue = 2
        return
      }
      failList.clear()
      failList.putAll(failDropHash)
      dropStatus.intValue = 1
      return
    }

    override fun onFailure(
      call: Call<HashMap<String, List<CourseFailDrop>>?>,
      t: Throwable
    ) {
      dropStatus.intValue = 2
      return
    }

  })


}
