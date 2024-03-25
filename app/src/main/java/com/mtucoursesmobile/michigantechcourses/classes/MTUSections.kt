package com.mtucoursesmobile.michigantechcourses.classes

data class MTUSections(
  val id: String,
  val courseId: String,
  val crn: String,
  val section: String,
  val cmp: String,
  val minCredits: Number,
  val maxCredits: Number,
  val time: SectionTimeRoot,
  var totalSeats: Number,
  var takenSeats: Number,
  var availableSeats: Number,
  var fee: Number,
  var updatedAt: String,
  var deletedAt: String?,
  var room: String,
  var buildingName: String,
  var localType: String,
  var instructors: Array<SectionInstructors>
)

data class SectionTimeRoot(
  var type: String,
  var rdates: SectionTimeRDates,
  var rrules: Array<SectionTimeRRules>,
  var exdates: SectionTimeExDates,
  var exrules: Array<Any>,
  var timezone: String
)

data class SectionTimeRDates(
  var type: String,
  var dates: Array<String>,
)

data class SectionTimeRRules(
  var type: String,
  var config: SectionTimeRRulesConfig
)

data class SectionTimeRRulesConfig(
  var end: SectionTimeRRulesConfigDates,
  var start: SectionTimeRRulesConfigDates,
  var duration: Number,
  var frequency: String,
  var byDayOfWeek: Array<String>
)

data class SectionTimeRRulesConfigDates(
  var day: Number,
  var hour: Number,
  var year: Number,
  var month: Number,
  var minute: Number,
  var second: Number,
  var timezone: String,
  var millisecond: Number
)

data class SectionTimeExDates(
  var type: String,
  var dates: Array<Any>
)

data class SectionInstructors(
  var id: Number
)