package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.localStore.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStore.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.localStore.MTUCoursesEntry
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
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

fun getSemesterCourses(courseList: MutableList<MTUCourses>, ctx: Context, semester: String, year: String) {
  val db = Room.databaseBuilder(
    ctx,
    AppDatabase::class.java, "mtucourses-db"
  ).addTypeConverter(MTUCoursesConverter()).allowMainThreadQueries().build()

  val cacheSize = 10 * 1024 * 1024 // 10 MB
  val cache = Cache(ctx.cacheDir, cacheSize.toLong())
  val okHttpClient = OkHttpClient.Builder()
    .cache(cache)
    .build()

  val courseDao = db.courseDao()

  val findDB: List<MTUCoursesEntry> = courseDao.getSpecificCourseEntry(semester, year)

  if (findDB.isEmpty()) {
    Log.d("SQL", "Nada")
  } else {
    Log.d("SQL", "me exist")
    courseList.clear()
    courseList.addAll(findDB[0].entry!!)
    return
  }


  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
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
        var lst: ArrayList<MTUCourses> = ArrayList()

        lst = response.body()!!

        if (courseList.size != 0) {
          courseList.clear()
        }

        for (i in 0 until lst.size) {
          courseList.add(lst[i])
        }
        courseDao.insertCourseEntry(MTUCoursesEntry(semester, year, courseList))
        return
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