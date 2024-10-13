package com.mtucoursesmobile.michigantechcourses.utils

fun CalculateClassLength(
  startHour: Int, startMinute: Int, endHour: Int, endMinute: Int
): Int {
  return (endHour * 60 + endMinute) - (startHour * 60 + startMinute)
}