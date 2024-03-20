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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
        Surface(
          // on below line we are specifying modifier and color for our app
          modifier = Modifier.fillMaxSize(),
        ) {
          // on the below line we are specifying the theme as scaffold.
          Scaffold(

            // in scaffold we are specifying top bar.
            topBar = {

              // inside top bar we are specifying background color.
              TopAppBar(

                // along with that we are specifying title for our top bar.
                title = {

                  // in the top bar we are specifying tile as a text
                  Text(

                    // on below line we are specifying
                    // text to display in top app bar.
                    text = "JSON Parsing in Android",

                    // on below line we are specifying
                    // modifier to fill max width.
                    modifier = Modifier.fillMaxWidth(),

                    // on below line we are
                    // specifying text alignment.
                    textAlign = TextAlign.Center,

                    // on below line we are
                    // specifying color for our text.
                    color = Color.White
                  )
                })
            }) { innerPadding ->
            val context = LocalContext.current

            val courseList = remember {
              mutableStateListOf<MTUCourses>()
            }
            getCourses(
              courseList,
              context
            )
            // on below line we are display list view
            // method to display our list view.
            DisplayListView(
              innerPadding,
              courseList
            )
          }
        }
      }
    }
  }
}

@Composable
fun DisplayListView(innerPadding: PaddingValues, courseList: SnapshotStateList<MTUCourses>) {
  LazyColumn(modifier = Modifier.padding(innerPadding)) {
    items(courseList.size) { item ->
      Text(
        courseList[item].toString(),
        modifier = Modifier.padding(15.dp)
      )
      Divider()
    }
  }
}