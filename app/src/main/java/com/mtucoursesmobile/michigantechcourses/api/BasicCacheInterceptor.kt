package com.mtucoursesmobile.michigantechcourses.api

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class BasicCacheInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val response: Response = chain.proceed(chain.request())
    val cacheControl = CacheControl.Builder()
      .maxAge(
        1,
        TimeUnit.DAYS
      )
      .build()
    return response.newBuilder()
      .header(
        "Cache-Interceptor",
        cacheControl.toString()
      )
      .build()
  }
}