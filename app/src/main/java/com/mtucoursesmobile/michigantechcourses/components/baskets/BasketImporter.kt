package com.mtucoursesmobile.michigantechcourses.components.baskets

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.mtucoursesmobile.michigantechcourses.classes.CurrentSemester
import com.mtucoursesmobile.michigantechcourses.classes.MTUSections
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun BasketImporter(
  basketImportContent: String,
  context: Context,
  attemptedBasketImport: MutableState<Boolean>,
  courseStatus: MutableIntState,
  sectionStatus: MutableIntState,
  semesterStatus: MutableIntState,
  instructorStatus: MutableIntState,
  buildingStatus: MutableIntState,
  dropStatus: MutableIntState,
  sectionList: MutableMap<String, MutableList<MTUSections>>,
  currentSemester: CurrentSemester,
  getSemesterBaskets: (CurrentSemester) -> Unit,
  setSemester: (CurrentSemester) -> Unit,
  importBasket: (CurrentSemester, String, List<String>, MutableMap<String, MutableList<MTUSections>>) -> Unit,
  basketStatus: MutableIntState

) {
  val invalidBasket = {
    Log.e(
      "BasketView",
      "Invalid basket import content: $basketImportContent"
    )
    Toast.makeText(
      context,
      "Invalid basket import content",
      Toast.LENGTH_SHORT
    ).show()
    attemptedBasketImport.value = true
  }
  if (!basketImportContent.startsWith("MTUANDROID:")
  ) {
    try {
      if (!Base64.decode(basketImportContent)
          .decodeToString().startsWith("MTUANDROID:")
      ) {
        invalidBasket()
        return
      }
    } catch (e: Exception) {
      invalidBasket()
      return
    }
  }
  val basketData = mutableMapOf<String, String>()
  val requestBody = try {
    Base64.decode(basketImportContent).decodeToString().substring(11)
  } catch (e: Exception) {
    basketImportContent.substring(11)
  }
  requestBody.split("&").forEach { pair ->
    val (key, value) = pair.split("=")
    basketData[key] = value
  }
  if (basketData["SEMESTER"] == null ||
    basketData["CRNS"] == null ||
    basketData["BASKET_NAME"] == null
  ) {
    invalidBasket()
    return
  }
  if (basketData["SEMESTER"]?.startsWith("SPRING") == false &&
    basketData["SEMESTER"]?.startsWith("SUMMER") == false &&
    basketData["SEMESTER"]?.startsWith("FALL") == false
  ) {
    invalidBasket()
    return
  }
  val sharerName = if (basketData["NAME"] == "") null else basketData["NAME"]
  val (semester, year) = basketData["SEMESTER"]!!.split("-")
  val crns = basketData["CRNS"]!!.split(",")
  val basketName = basketData["BASKET_NAME"]!!

  var initialDialogIsOpen by remember { mutableStateOf(true) }
  var secondaryDialogIsOpen by remember { mutableStateOf(false) }
  when {
    initialDialogIsOpen -> {
      AlertDialog(
        title = { Text(text = "Import $basketName?") },
        text = {
          Text(
            text = "${sharerName ?: "Someone"} shared their ${
              semester.substring(
                0,
                1
              ) + semester.substring(1).toLowerCase(locale = Locale.current)
            } $year basket with you! Would you like to start importing it?"
          )
        },
        onDismissRequest = {
          initialDialogIsOpen = false
          attemptedBasketImport.value = true
        },
        confirmButton = {
          TextButton(onClick = {
            initialDialogIsOpen = false
            secondaryDialogIsOpen = true
          }) {
            Text(text = "Yes")
          }
        },
        dismissButton = {
          TextButton(onClick = {
            initialDialogIsOpen = false
            attemptedBasketImport.value = true
          }) {
            Text(text = "No")
          }
        }
      )
    }
  }
  when {
    secondaryDialogIsOpen -> {
      val newSemester = remember {
        CurrentSemester(
          "${
            semester.lowercase().replaceFirstChar(Char::titlecase)
          } $year",
          year,
          semester
        )
      }
      LaunchedEffect(Unit) {
        setSemester(newSemester)
        getSemesterBaskets(newSemester)
      }
      AlertDialog(
        title = {
          Text(
            text = "${
              if (courseStatus.intValue != 1 ||
                sectionStatus.intValue != 1 ||
                semesterStatus.intValue != 1 ||
                instructorStatus.intValue != 1 ||
                buildingStatus.intValue != 1 ||
                dropStatus.intValue != 1 ||
                basketStatus.intValue != 1
              ) "Loading" else "Confirm Import of "
            } $basketName${
              if (courseStatus.intValue != 1 ||
                sectionStatus.intValue != 1 ||
                semesterStatus.intValue != 1 ||
                instructorStatus.intValue != 1 ||
                buildingStatus.intValue != 1 ||
                dropStatus.intValue != 1 ||
                basketStatus.intValue != 1
              ) "..." else ""
            }"
          )
        },
        text = {
          Text(
            text = if (courseStatus.intValue != 1 ||
              sectionStatus.intValue != 1 ||
              semesterStatus.intValue != 1 ||
              instructorStatus.intValue != 1 ||
              buildingStatus.intValue != 1 ||
              dropStatus.intValue != 1 ||
              basketStatus.intValue != 1
            ) "Please wait while the basket information is being loaded..." else "$basketName has ${crns.size} sections in it. Would you like to import it?"
          )
        },
        onDismissRequest = {
          secondaryDialogIsOpen = false
          attemptedBasketImport.value = true
        },
        confirmButton = {
          TextButton(
            onClick = {
              importBasket(
                newSemester,
                basketName,
                crns,
                sectionList
              )
              secondaryDialogIsOpen = false
              Toast.makeText(
                context,
                "Importing $basketName...",
                Toast.LENGTH_SHORT
              ).show()
              attemptedBasketImport.value = true

            },
            enabled = !(courseStatus.intValue != 1 ||
                sectionStatus.intValue != 1 ||
                semesterStatus.intValue != 1 ||
                instructorStatus.intValue != 1 ||
                buildingStatus.intValue != 1 ||
                dropStatus.intValue != 1 ||
                basketStatus.intValue != 1)
          ) {
            Text(text = "Confirm")
          }
        },
        dismissButton = {
          TextButton(onClick = {
            secondaryDialogIsOpen = false
            attemptedBasketImport.value = true
          }) {
            Text(text = "Cancel")
          }
        }
      )
    }
  }
}