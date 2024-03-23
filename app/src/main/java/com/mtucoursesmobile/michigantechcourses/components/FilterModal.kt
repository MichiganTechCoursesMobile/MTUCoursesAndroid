package com.mtucoursesmobile.michigantechcourses.components

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CourseFilterViewModel
import com.mtucoursesmobile.michigantechcourses.viewModels.CurrentSemesterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModal(ctx: Context) {
  val courseFilterViewModel: CourseFilterViewModel = viewModel()
  val currentSemesterViewModel: CurrentSemesterViewModel = viewModel()
  BottomSheetDefaults.windowInsets
  if (courseFilterViewModel.showFilter.value) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      onDismissRequest = {
        courseFilterViewModel.showFilter.value = false
      }
    ) {
      CollapsableList(
        sections = listOf(
          CollapsableListSection(
            {
              Text(
                text = "Course Type",
                style = MaterialTheme.typography.bodyLarge
              )
            },
            rows = listOf {
              Column {
                courseFilterViewModel.courseTypes.forEach() { it ->
                  CoolCheckBox(
                    text = it.first,
                    action = it.second
                  )
                }
              }
            }
          ),
          CollapsableListSection(
            {
              Text(
                text = "Course Level",
                style = MaterialTheme.typography.bodyLarge
              )
            },
            rows = listOf {
              Column {
                courseFilterViewModel.courseLevels.forEach() { it ->
                  CoolCheckBox(
                    text = it.first,
                    action = it.second
                  )
                }
              }
            }
          ),
          CollapsableListSection(
            { Text(text = "Course Credits") },
            rows = listOf {
              Column {
                courseFilterViewModel.courseCredits.forEach() { it ->
                  CoolCheckBox(
                    text = it.first,
                    action = it.second
                  )
                }
              }
            }
          ),
          CollapsableListSection(
            { Text(text = "Other") },
            rows = listOf {
              Column {
                courseFilterViewModel.otherCourseFilters.forEach() { it ->
                  CoolCheckBox(
                    text = it.first,
                    action = it.second
                  )
                }
              }
            }
          )
        ),
      )

    }
  }
}

@Composable
fun CoolCheckBox(text: String, action: MutableState<Boolean>) {
  Row(
    Modifier
      .fillMaxWidth()
      .height(56.dp)
      .toggleable(
        value = action.value,
        onValueChange = {
          action.value = (!action.value)
        },
        role = Role.Checkbox
      )
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
      checked = action.value,
      onCheckedChange = null,
      modifier = Modifier.padding(start = 32.dp)
    )
    Text(
      text = text,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(start = 16.dp)
    )
  }
}


@Composable
fun CollapsableList(
  sections: List<CollapsableListSection>,
  modifier: Modifier = Modifier.padding(bottom = 40.dp),
  state: LazyListState = rememberLazyListState(),
  expandByDefault: Boolean = false
) {
  val expandState =
    remember(sections) { sections.map { expandByDefault }.toMutableStateList() }

  LazyColumn(
    modifier,
    state = state
  ) {
    sections.forEachIndexed { i, section ->
      val expand = expandState[i]
      item(key = "header_$i") {
        CollapsableListRow(
          {
            Row(
              modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
            ) {
              Crossfade(targetState = expandState) { state ->
                when (state[i]) {
                  true -> Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "Arrow",
                    modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
                  )

                  false -> Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Arrow",
                    modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
                  )
                }
              }

              section.header()
            }
          },
          Modifier.clickable { expandState[i] = !expand }
        )
      }

      items(section.rows) { row ->
        when (expand) {
          true -> CollapsableListRow({ row() })
          false -> {}
        }
      }
    }
  }
}

@Composable
private fun CollapsableListRow(content: @Composable () -> Unit, modifier: Modifier = Modifier) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .then(modifier)
  ) {
    content()
  }
}

data class CollapsableListSection(
  val header: @Composable () -> Unit,
  val rows: List<@Composable () -> Unit>
)