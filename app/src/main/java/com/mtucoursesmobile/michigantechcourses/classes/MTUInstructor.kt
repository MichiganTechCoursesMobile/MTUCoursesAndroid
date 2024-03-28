package com.mtucoursesmobile.michigantechcourses.classes

data class MTUInstructor(
  var id: Number,
  var email: String?,
  var phone: String?,
  var rmpId: String?,
  var office: String?,
  var fullName: String,
  var deletedAt: String?,
  var interests: List<String>,
  var updatedAt: String,
  var numRatings: Number?,
  var websiteURL: String?,
  var departments: List<String>,
  var occupations: List<String>,
  var averageRating: Number,
  var averageDifficultyRating: Number,
  var thumbnailURL: String?
)