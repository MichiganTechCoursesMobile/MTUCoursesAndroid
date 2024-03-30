package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.viewModels.MTUCoursesViewModel
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalLayoutApi::class
)
@Composable
fun FilterModal(
  listState: LazyListState,
  courseViewModel: MTUCoursesViewModel
) {
  val scope = rememberCoroutineScope()
  BottomSheetDefaults.windowInsets
  if (courseViewModel.showFilter.value) {

    ModalBottomSheet(sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      onDismissRequest = {
        courseViewModel.showFilter.value = false
      }) {
      //Sorting
      Text(
        text = "Sort By",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .padding(bottom = 8.dp)
      )
      Column {
        FlowRow(
          horizontalArrangement = Arrangement.Start,
          modifier = Modifier
            .padding(bottom = 8.dp)
            .padding(horizontal = 28.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top)
        ) {
          for (type in courseViewModel.sortingTypes) {
            FilterChip(
              selected = courseViewModel.sortingMode.value.first == type.key,
              onClick = {
                if (courseViewModel.sortingMode.value.first == type.key) {
                  if (courseViewModel.sortingMode.value.second == "ascending") {
                    courseViewModel.sortingMode.value = Pair(
                      type.key,
                      "descending"
                    )
                  } else {
                    courseViewModel.sortingMode.value = Pair(
                      type.key,
                      "ascending"
                    )
                  }
                } else {
                  courseViewModel.sortingMode.value = type.toPair()
                }
              },
              label = { Text(text = type.key) },
              leadingIcon = {
                if (courseViewModel.sortingMode.value.first == type.key) {
                  Icon(
                    imageVector = if (courseViewModel.sortingMode.value.second == "ascending") Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Arrow Up"
                  )
                }
              },
              modifier = Modifier.padding(end = 4.dp)
            )
          }
        }
      }
      // Level
      var levelSliderPosition by remember {
        mutableStateOf(courseViewModel.courseLevelFilter.value)
      }
      when (levelSliderPosition.toString()) {
        "1.0..1.0" -> Text(
          text = "Course Level: 1000",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )

        "4.0..4.0" -> Text(
          text = "Course Level: 4000+",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )

        else -> Text(
          text = "Course Level: ${
            levelSliderPosition.toString().first()
          }000-${
            levelSliderPosition.toString().substring(
              5,
              6
            )
          }000${
            if (levelSliderPosition.toString().substring(
                5,
                6
              ) == "4"
            ) "+" else ""
          }",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )
      }
      RangeSlider(
        value = levelSliderPosition,
        onValueChange = { range -> levelSliderPosition = range },
        steps = 2,
        valueRange = 1f..4f,
        onValueChangeFinished = {
          scope.launch {
            courseViewModel.toggleLevel(levelSliderPosition)
            listState.animateScrollToItem(0)
          }
        },
        modifier = Modifier
          .padding(horizontal = 32.dp)
          .padding(bottom = 8.dp)
      )

      //Credits
      var creditSliderPosition by remember {
        mutableStateOf(courseViewModel.courseCreditFilter.value)
      }
      when (creditSliderPosition.toString()) {
        "0.0..0.0" -> Text(
          text = "Course Credits: ≤ 1",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )

        "4.0..4.0" -> Text(
          text = "Course Credits: ≥ 4",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )

        else -> Text(
          text = "Course Credits: ${
            creditSliderPosition.toString().first()
          }-${
            creditSliderPosition.toString().substring(
              5,
              6
            )
          }",
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 16.dp)
        )
      }
      RangeSlider(
        value = creditSliderPosition,
        onValueChange = { range -> creditSliderPosition = range },
        steps = 3,
        valueRange = 0f..4f,
        onValueChangeFinished = {
          scope.launch {
            courseViewModel.toggleCredit(creditSliderPosition)
            listState.animateScrollToItem(0)
          }
        },
        modifier = Modifier
          .padding(horizontal = 32.dp)
          .padding(bottom = 8.dp)
      )
      //Other
      Text(
        text = "Other",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .padding(bottom = 8.dp)
      )
      Column {
        FlowRow(
          horizontalArrangement = Arrangement.Start,
          modifier = Modifier
            .padding(bottom = 60.dp)
            .padding(horizontal = 28.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top)
        ) {
          courseViewModel.otherCourseFilters.forEach() { it ->
            val (checked, onCheckChange) = remember {
              mutableStateOf(it.second.value)
            }
            FilterChip(selected = checked,
              onClick = {
                onCheckChange(!checked)
                it.second.value = !it.second.value
                scope.launch {
                  courseViewModel.toggleOther(it.first)
                  listState.animateScrollToItem(0)
                }
              },
              label = { Text(text = it.first) },
              modifier = Modifier.padding(end = 4.dp),
              leadingIcon = if (checked) {
                {
                  Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = "Check Mark",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                  )
                }
              } else {
                null
              })
          }
        }
      }
    }
  }
}
