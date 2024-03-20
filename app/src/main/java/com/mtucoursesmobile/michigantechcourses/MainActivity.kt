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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mtucoursesmobile.michigantechcourses.api.MTUCourses
import com.mtucoursesmobile.michigantechcourses.api.getCourses
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
        data class CurrentSemester(
          val readable: String,
          val year: String,
          val semester: String
        )

        var expanded by remember { mutableStateOf(false) }
        val semesterList = arrayListOf(
          CurrentSemester(
            "Fall 2024",
            "2024",
            "FALL"
          ),
          CurrentSemester(
            "Spring 2024",
            "2024",
            "SPRING"
          ),
          CurrentSemester(
            "Summer 2024",
            "2024",
            "SUMMER"
          )
        )
        var currentSemester by remember {
          mutableStateOf(
            CurrentSemester(
              "Fall 2024",
              "2024",
              "FALL"
            )
          )
        }
        Scaffold(
          topBar = {
            TopAppBar(colors = topAppBarColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              titleContentColor = MaterialTheme.colorScheme.primary
            ),
              title = {
                OutlinedButton(onClick = { expanded = true }) {
                  Text(currentSemester.readable)
                }
                DropdownMenu(
                  expanded = expanded,
                  onDismissRequest = { expanded = false }) {
                  for (i in semesterList) {
                    DropdownMenuItem(
                      text = { Text(i.readable) },
                      onClick = {
                        currentSemester = i
                        expanded = false
                      })
                  }
                }
              })
          }) { innerPadding ->
          val context = LocalContext.current

          val courseList = remember {
            mutableStateListOf<MTUCourses>()
          }
          getCourses(
            courseList,
            context,
            currentSemester.semester,
            currentSemester.year
          )
          // on below line we are display list view
          // method to display our list view.
          DisplayCourseList(
            innerPadding,
            courseList
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayCourseList(innerPadding: PaddingValues, courseList: SnapshotStateList<MTUCourses>) {
  LazyColumn(
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(courseList.size) { item ->
      ElevatedCard(
        elevation = CardDefaults.cardElevation(
          defaultElevation = 4.dp
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(10.dp),
      ) {
        Text(
          text = "${courseList[item].title} (${courseList[item].semester} ${courseList[item].year})",
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
          text = if (courseList[item].description != null) courseList[item].description else "¯\\_(ツ)_/¯",
          modifier = Modifier.padding(horizontal = 10.dp),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}