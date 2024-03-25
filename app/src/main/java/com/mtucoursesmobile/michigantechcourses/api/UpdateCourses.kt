package com.mtucoursesmobile.michigantechcourses.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mtucoursesmobile.michigantechcourses.classes.MTUCourses
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesEntry
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant

interface RetroFitAPIDate {
  @GET("sections")
  fun updatedSectionData(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<ArrayList<MTUSections>>

  @GET("courses")
  fun updatedCourseData(
    @Query("semester") semester: String, @Query("year") year: String,
    @Query("updatedSince") lastUpdated: String
  ): Call<ArrayList<MTUCourses>>
}

@OptIn(DelicateCoroutinesApi::class)
fun updateSemesterCourses(
  courseList: MutableList<MTUCoursesEntry>, ctx: Context, semester: String, year: String,
  courseDB: AppDatabase
) {
  val courseDao = courseDB.courseDao()
  val dateDao = courseDB.courseDao()

  val data = dateDao.getSemesterDate(
    semester,
    year
  )

  Log.d(
    "DEBUG",
    data.toString()
  )

  val lastUpdated = data.lastUpdated

  val cacheSize = 100 * 1024 * 1024
  val cache = Cache(
    ctx.cacheDir,
    cacheSize.toLong()
  )
  val okHttpClient = OkHttpClient.Builder()
    .cache(cache)
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("https://api.michigantechcourses.com/").client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()
  val retrofitAPI = retrofit.create(RetroFitAPIDate::class.java)

  val courseCall: Call<ArrayList<MTUCourses>> = retrofitAPI.updatedCourseData(
    semester,
    year,
    lastUpdated
  )

  val sectionCall: Call<ArrayList<MTUSections>> = retrofitAPI.updatedSectionData(
    semester,
    year,
    lastUpdated
  )

  courseCall!!.enqueue(object : Callback<ArrayList<MTUCourses>?> {
    override fun onResponse(
      call: Call<ArrayList<MTUCourses>?>,
      response: Response<ArrayList<MTUCourses>?>
    ) {
      if (response.isSuccessful) {
        val courseData: ArrayList<MTUCourses> = response.body()!!
        sectionCall!!.enqueue(object : Callback<ArrayList<MTUSections>?> {
          override fun onResponse(
            call: Call<ArrayList<MTUSections>?>, response: Response<ArrayList<MTUSections>?>
          ) {
            if (response.isSuccessful) {
              val sectionData: ArrayList<MTUSections> = response.body()!!
              val timeGot = Instant.now().toString()
              if (sectionData.size == 0 && courseData.size == 0) {
                Toast.makeText(
                  ctx,
                  "Nothing to update...",
                  Toast.LENGTH_SHORT
                ).show()
                return
              } else {
                Toast.makeText(
                  ctx,
                  "Found data",
                  Toast.LENGTH_SHORT
                ).show()
                GlobalScope.launch(Dispatchers.Default) {
                  val coursesToYeet = mutableListOf<MTUCoursesEntry>()
                  val sectionsToYeet = mutableListOf<Pair<MTUCoursesEntry, MTUSections>>()
                  val sectionsToUpdate = mutableListOf<Pair<MTUCoursesEntry, MTUSections>>()
                  for (i in courseData) {
                    if (i.deletedAt != null) {
                      courseList.find { entry -> i.id == entry.courseId }
                        ?.let { coursesToYeet.add(it) }
                    } else {
                      courseList.find { entry -> i.id == entry.courseId }?.let { j ->
                        val iterator = j.entry.course.listIterator()
                        while (iterator.hasNext()) {
                          val course = iterator.next()
                          if (course.id == i.id) {
                            iterator.set(i)
                          }
                        }
                      }
                    }
                  }
                  for (i in sectionData) {
                    if (i.deletedAt != null) {
                      courseList.find { entry -> i.courseId == entry.courseId }?.let { course ->
                        course.entry.sections.find { section -> i.id == section.id }?.let {
                          sectionsToYeet.add(
                            Pair(
                              course,
                              it
                            )
                          )
                        }
                      }
                    } else {
                      courseList.find { entry -> i.courseId == entry.courseId }?.let { course ->
                        course.entry.sections.find { section -> i.id == section.id }?.let {
                          sectionsToUpdate.add(
                            Pair(
                              course,
                              it
                            )
                          )
                        }
                      }
                    }
                  }
                  for (i in sectionsToYeet) {
                    courseList.find { course -> course.courseId == i.first.courseId }?.let {
                      val iterator = it.entry.sections.iterator()
                      while (iterator.hasNext()) {
                        val section = iterator.next()
                        if (section.id == i.second.id) {
                          iterator.remove()
                        }
                      }
                    }
                  }
                  for (i in sectionsToUpdate) {
                    courseList.find { course -> course.courseId == i.first.courseId }?.let {
                      val iterator = it.entry.sections.listIterator()
                      while (iterator.hasNext()) {
                        val oldSection = iterator.next()
                        if (oldSection.id == i.second.id) iterator.set(i.second)
                      }
                    }
                  }
                  courseList.removeAll(coursesToYeet)
                }
                return
              }
            }
          }

          override fun onFailure(call: Call<ArrayList<MTUSections>?>, t: Throwable) {
            Log.d(
              "DEBUG",
              t.cause.toString()
            );
            Toast.makeText(
              ctx,
              "Failed to get data..",
              Toast.LENGTH_SHORT
            ).show()
          }
        })
      }
      return
    }

    override fun onFailure(call: Call<ArrayList<MTUCourses>?>, t: Throwable) {
      Log.d(
        "DEBUG",
        t.cause.toString()
      );
      Toast.makeText(
        ctx,
        "Failed to get data..",
        Toast.LENGTH_SHORT
      ).show()

    }
  })

}