package com.mtucoursesmobile.michigantechcourses

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL


data class MTUCourses(
  val id: String, val year: Int, val semester: String, val subject: String, val crse: String,
  val title: String,
  val description: String, val updatedAt: String, val deletedAt: String, val prereqs: String,
  val offered: Array<String>, val minCredits: Double, val maxCredits: Double
)

fun getJSON(): Array<MTUCourses> {
  var data =
    URL("https://api.michigantechcourses.com/courses?semester=FALL&year=2024").readText()
  val gson = Gson()
  val arrayTutorialType = object : TypeToken<Array<MTUCourses>>() {}.type

  var tutorials: Array<MTUCourses> = gson.fromJson(
    data,
    arrayTutorialType
  )
  return tutorials
}

class MainActivity : ComponentActivity() {


  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    val policy = ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
        val JSONData: Array<MTUCourses> = runBlocking { getJSON() }
        Log.d(
          "hi",
          JSONData.size.toString()
        )
        Scaffold(
          bottomBar = {
            BottomAppBar(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.primary,
            ) {
              Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Bottom"
              )
            }
          },
          floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
              Icon(
                Icons.Default.Add,
                contentDescription = "Add"
              )
            }
          }) { innerPadding ->
          CourseList(
            innerPadding,
            JSONData
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseList(innerPadding: PaddingValues, JSONData: Array<MTUCourses>) {
  LazyColumn(
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(JSONData.size) { item ->
      val sheetState = rememberModalBottomSheetState()
      val scope = rememberCoroutineScope()
      var showBottomSheet by remember {
        mutableStateOf(false)
      }
      ElevatedCard(
        elevation = CardDefaults.cardElevation(
          defaultElevation = 4.dp
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(10.dp),
        onClick = {
          showBottomSheet = true
        }
      ) {
        Text(
          text = "${JSONData[item].subject}${JSONData[item].crse} - ${JSONData[item].title}",
          modifier = Modifier
            .padding(
              horizontal = 10.dp
            )
            .paddingFromBaseline(
              top = 30.dp,
              bottom = 10.dp
            ),
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          textAlign = TextAlign.Center,
        )
        Text(
          text = JSONData[item].description,
          modifier = Modifier.padding(horizontal = 10.dp),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis
        )
      }
      if (showBottomSheet) {
        ModalBottomSheet(
          onDismissRequest = { showBottomSheet = false },
          sheetState = sheetState,
          modifier = Modifier.height(500.dp)
        ) {
          Text(
            text = "${JSONData[item].subject}${JSONData[item].crse} - ${JSONData[item].title}",
            modifier = Modifier
              .padding(
                horizontal = 10.dp
              )
              .paddingFromBaseline(
                top = 30.dp,
                bottom = 10.dp
              ),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
          )
          Text(
            text = JSONData[item].description,
            modifier = Modifier.padding(horizontal = 10.dp),
          )
          Button(
            onClick = {
              scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                  showBottomSheet = false
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp)
          ) {
            Text("Hide")
          }

        }
      }
    }
  }
}