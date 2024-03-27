package com.mtucoursesmobile.michigantechcourses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
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
import androidx.compose.ui.unit.dp
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)
@Composable
fun FilterModal(
  listState: LazyListState,
  courseFilterViewModel: CourseFilterViewModel
) {
  val scope = rememberCoroutineScope()
  BottomSheetDefaults.windowInsets
  if (courseFilterViewModel.showFilter.value) {

    ModalBottomSheet(sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      onDismissRequest = {
        courseFilterViewModel.showFilter.value = false
      }) {
      //Type
      Text(
        text = "Course Type",
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
            .padding(horizontal = 28.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top)
        ) {
          courseFilterViewModel.courseTypes.forEach() { it ->
            val (checked, onCheckChange) = remember {
              mutableStateOf(it.second.value)
            }
            FilterChip(selected = checked,
              onClick = {
                onCheckChange(!checked)
                it.second.value = !it.second.value
                scope.launch {
                  courseFilterViewModel.toggleType(it.first)
                  listState.animateScrollToItem(0)
                }
              },
              label = { Text(text = it.first) },
              modifier = Modifier.padding(horizontal = 4.dp),
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
      // Level
      var levelSliderPosition by remember {
        mutableStateOf(courseFilterViewModel.levelFilter.value)
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
              5, 6
            )
          }000${
            if (levelSliderPosition.toString().substring(
                5, 6
              ) == "4"
            ) "+" else ""
          }", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp)
        )
      }
      RangeSlider(
        value = levelSliderPosition,
        onValueChange = { range -> levelSliderPosition = range },
        steps = 2,
        valueRange = 1f..4f,
        onValueChangeFinished = {
          courseFilterViewModel.toggleLevel(levelSliderPosition)
          scope.launch {
            listState.animateScrollToItem(0)
          }
        },
        modifier = Modifier
          .padding(horizontal = 32.dp)
          .padding(bottom = 8.dp)
      )

      //Credits
      var creditSliderPosition by remember {
        mutableStateOf(courseFilterViewModel.creditFilter.value)
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
              5, 6
            )
          }", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp)
        )
      }
      RangeSlider(
        value = creditSliderPosition,
        onValueChange = { range -> creditSliderPosition = range },
        steps = 3,
        valueRange = 0f..4f,
        onValueChangeFinished = {
          courseFilterViewModel.toggleCredit(creditSliderPosition)
          scope.launch {
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
          courseFilterViewModel.otherCourseFilters.forEach() { it ->
            val (checked, onCheckChange) = remember {
              mutableStateOf(it.second.value)
            }
            FilterChip(selected = checked,
              onClick = {
                onCheckChange(!checked)
                it.second.value = !it.second.value
                scope.launch {
                  courseFilterViewModel.toggleOther(it.first)
                  listState.animateScrollToItem(0)
                }
              },
              label = { Text(text = it.first) },
              modifier = Modifier.padding(horizontal = 4.dp),
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
