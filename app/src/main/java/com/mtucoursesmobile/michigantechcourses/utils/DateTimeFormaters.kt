package com.mtucoursesmobile.michigantechcourses.utils

import android.icu.text.SimpleDateFormat
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import com.mtucoursesmobile.michigantechcourses.localStorage.DateFormat
import java.util.Locale

fun sectionTimeFormatter(section: MTUSections): String {
  val dow = StringBuilder()
  val tod = StringBuilder()
  if (section.time.rrules.isEmpty() || section.time.rrules[0].config.byDayOfWeek.isEmpty()) {
    dow.append("¯\\_(ツ)_/¯")
  } else {
    for (day in section.time.rrules[0].config.byDayOfWeek) {
      if (day == "TH") {
        dow.append("R")
      } else {
        dow.append(
          day.substring(
            0,
            day.length - 1
          )
        )
      }
    }
  }
  if (section.time.rrules.isNotEmpty()) {
    val tempStartTime = SimpleDateFormat(
      "HH:mm",
      Locale.ENGLISH
    ).parse("${section.time.rrules[0].config.start.hour}:${section.time.rrules[0].config.start.minute}")
    val tempEndTime = SimpleDateFormat(
      "HH:mm",
      Locale.ENGLISH
    ).parse("${section.time.rrules[0].config.end.hour}:${section.time.rrules[0].config.end.minute}")
    tod.append(
      SimpleDateFormat(
        "H:mma",
        Locale.ENGLISH
      ).format(tempStartTime).toString()
    )
    tod.append(" - ")
    tod.append(
      SimpleDateFormat(
        "H:mma",
        Locale.ENGLISH
      ).format(tempEndTime).toString()
    )
  }
  return "$dow $tod"
}

fun sectionDateFormatter(
  section: MTUSections,
  dateFormat: DateFormat
): String {
  return if (section.time.rrules.isEmpty()) {
    "¯\\_(ツ)_/¯"
  } else {
    when (dateFormat) {
      DateFormat.MDY -> {
        "${section.time.rrules[0].config.start.month}/${section.time.rrules[0].config.start.day}/${section.time.rrules[0].config.start.year} - ${section.time.rrules[0].config.end.month}/${section.time.rrules[0].config.end.day}/${section.time.rrules[0].config.end.year}"
      }

      DateFormat.DMY -> {
        "${section.time.rrules[0].config.start.day}/${section.time.rrules[0].config.start.month}/${section.time.rrules[0].config.start.year} - ${section.time.rrules[0].config.end.day}/${section.time.rrules[0].config.end.month}/${section.time.rrules[0].config.end.year}"
      }

      DateFormat.YMD -> {
        "${section.time.rrules[0].config.start.year}/${section.time.rrules[0].config.start.month}/${section.time.rrules[0].config.start.day} - ${section.time.rrules[0].config.end.year}/${section.time.rrules[0].config.end.month}/${section.time.rrules[0].config.end.day}"
      }
    }
  }
}