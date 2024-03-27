package com.mtucoursesmobile.michigantechcourses.views

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailView(
  semesterViewModel: CurrentSemesterViewModel,
  courseFilterViewModel: CourseFilterViewModel,
  navController: NavController,
  courseId: String?
) {
  val foundCourse =
    semesterViewModel.courseList.find { course -> course.courseId == courseId }?.entry
  if (foundCourse != null) {
    Scaffold(
      contentWindowInsets = WindowInsets(0.dp),
      topBar = {
        TopAppBar(
          title = { Text(text = foundCourse.course[0].title) },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary
          ),
          navigationIcon = {
            IconButton(onClick = {
              navController.popBackStack()
            }) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        )
      }) { innerPadding ->
      Column(
        Modifier
          .padding(innerPadding)
          .padding(horizontal = 8.dp)
      ) {
        var description = "¯\\_(ツ)_/¯"
        if (foundCourse.course[0].description != null) {
          description = foundCourse.course[0].description!!
        }
        ExpandableCard(
          title = "Course Description:",
          description = description
        )
        LazyColumn(
          modifier = Modifier
            .fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          itemsIndexed(
            items = foundCourse.sections.filter { section -> section?.deletedAt == null },
            key = { _, item -> item!!.id }) { _, item ->
            ElevatedCard(
              elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp),
            ) {
              Text(item?.section.toString())
            }
          }
        }
      }

    }
  }
}

@Composable
fun ExpandableCard(
  title: String,
  titleFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
  titleFontWeight: FontWeight = FontWeight.Bold,
  description: String,
  descriptionFontSize: TextUnit = MaterialTheme.typography.titleSmall.fontSize,
  descriptionFontWeight: FontWeight = FontWeight.Normal,
  descriptionMaxLines: Int = 3,
  shape: RoundedCornerShape = RoundedCornerShape(10.dp)
) {

  var expandedState by remember { mutableStateOf(false) }
  var showExpand by remember { mutableStateOf(true) }
  val rotationState by animateFloatAsState(
    targetValue = if (expandedState) 180f else 0f, label = "flip"
  )
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize(
        animationSpec = tween(
          durationMillis = 300,
          easing = LinearOutSlowInEasing
        )
      ),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    shape = shape,
    onClick = {
      expandedState = !expandedState
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .padding(bottom = 6.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          modifier = Modifier
            .weight(6f),
          text = title,
          fontSize = titleFontSize,
          fontWeight = titleFontWeight,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (showExpand) {
          IconButton(
            modifier = Modifier
              .weight(1f)
              .alpha(0.2f)
              .rotate(rotationState),
            onClick = {
              expandedState = !expandedState
            }) {
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Drop-Down Arrow"
            )
          }
        }
      }
      if (!expandedState) {
        Text(
          text = description,
          fontSize = descriptionFontSize,
          fontWeight = descriptionFontWeight,
          maxLines = descriptionMaxLines,
          overflow = TextOverflow.Ellipsis,
          onTextLayout = { textLayoutResult ->
            showExpand =
              textLayoutResult.isLineEllipsized(textLayoutResult.lineCount - 1)
          }
        )
      } else {
        Text(
          text = description,
          fontSize = descriptionFontSize,
          fontWeight = descriptionFontWeight,
        )
      }
    }
  }
}

