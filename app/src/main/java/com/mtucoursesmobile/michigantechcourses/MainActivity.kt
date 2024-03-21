package com.mtucoursesmobile.michigantechcourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.room.Room
import com.mtucoursesmobile.michigantechcourses.api.MTUCourses
import com.mtucoursesmobile.michigantechcourses.api.getSemesterCourses
import com.mtucoursesmobile.michigantechcourses.localStorage.AppDatabase
import com.mtucoursesmobile.michigantechcourses.localStorage.MTUCoursesConverter
import com.mtucoursesmobile.michigantechcourses.ui.theme.MichiganTechCoursesTheme

class MainActivity : ComponentActivity() {

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MichiganTechCoursesTheme {
        // Initialized the local storage DB
        val db = Room.databaseBuilder(
          LocalContext.current,
          AppDatabase::class.java,
          "mtucourses-db"
        ).addTypeConverter(MTUCoursesConverter()).allowMainThreadQueries().build()

        data class CurrentSemester(
          val readable: String,
          val year: String,
          val semester: String
        )

        var expanded by remember { mutableStateOf(false) }
        var searchBarValue by rememberSaveable { mutableStateOf("") }
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
          },
          floatingActionButton = {
            TextField(
              value = searchBarValue,
              onValueChange = { searchBarValue = it },
              label = { Text("Course Search") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
              shape = CircleShape,
              colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
              )
            )

          }) { innerPadding ->
          val context = LocalContext.current

          val courseList = remember {
            mutableStateListOf<MTUCourses>()
          }
          getSemesterCourses(
            courseList,
            context,
            currentSemester.semester,
            currentSemester.year,
            db
          )
          // on below line we are display list view
          // method to display our list view.
          DisplayCourseList(
            innerPadding,
            courseList,
            searchBarValue
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayCourseList(
  innerPadding: PaddingValues,
  courseList: SnapshotStateList<MTUCourses>,
  searchBarValue: String
) {
  LazyColumn(
    modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(courseList.filter { course -> course.title.contains(searchBarValue) }) { _, item ->
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
          text = "${item.subject}${item.crse} - ${item.title}",
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
          text = if (item.description != null) item.description else "¯\\_(ツ)_/¯",
          modifier = Modifier.padding(horizontal = 10.dp),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}