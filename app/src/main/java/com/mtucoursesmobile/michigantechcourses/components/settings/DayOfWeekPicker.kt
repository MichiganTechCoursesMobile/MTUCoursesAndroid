package com.mtucoursesmobile.michigantechcourses.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtucoursesmobile.michigantechcourses.localStorage.FirstDayOfWeek
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsModelProvider
import com.mtucoursesmobile.michigantechcourses.viewModels.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun DayOfWeekPicker() {
  val scope = rememberCoroutineScope()
  val settingsModel: SettingsViewModel = viewModel(factory = SettingsModelProvider.Factory)
  val firstDayOfWeek by settingsModel.firstDayOfWeek.collectAsState()
  var expanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize(
        animationSpec = tween(
          durationMillis = 300,
          easing = LinearOutSlowInEasing
        )
      )
      .padding(
        horizontal = 12.dp
      ),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    shape = RoundedCornerShape(12.dp),
  ) {
    Row(modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { expanded = !expanded }
      .padding(
        horizontal = 10.dp,
        vertical = 12.dp
      ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center

    ) {
      Icon(
        imageVector = Icons.Rounded.EditCalendar,
        contentDescription = "First Day of the Week",
        modifier = Modifier.padding(end = 8.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      Text(
        text = "First Day of the Week",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.weight(1f)
      )
      val rotate by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Arrow"
      )
      Icon(
        imageVector = Icons.Rounded.ArrowDropDown,
        contentDescription = "Drop Down",
        modifier = Modifier.rotate(rotate)
      )
    }
    AnimatedVisibility(expanded) {
      Column {
        Card(
          modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 4.dp
          ),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Row(
            Modifier
              .fillMaxWidth()
              .height(42.dp)
              .selectable(
                selected = (firstDayOfWeek == FirstDayOfWeek.SATURDAY),
                onClick = {
                  scope.launch {
                    settingsModel.updateFirstDayOfWeek(FirstDayOfWeek.SATURDAY)
                  }
                },
                role = Role.RadioButton
              )
              .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Saturday",
              Modifier.weight(1f)
            )
            RadioButton(
              selected = firstDayOfWeek == FirstDayOfWeek.SATURDAY,
              onClick = null
            )
          }
        }
        Card(
          modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 4.dp
          ),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Row(
            Modifier
              .fillMaxWidth()
              .height(42.dp)
              .selectable(
                selected = (firstDayOfWeek == FirstDayOfWeek.SUNDAY),
                onClick = {
                  scope.launch {
                    settingsModel.updateFirstDayOfWeek(FirstDayOfWeek.SUNDAY)
                  }
                },
                role = Role.RadioButton
              )
              .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Sunday",
              Modifier.weight(1f)
            )
            RadioButton(
              selected = firstDayOfWeek == FirstDayOfWeek.SUNDAY,
              onClick = null
            )
          }
        }
        Card(
          modifier = Modifier
            .padding(
              horizontal = 8.dp,
              vertical = 4.dp
            )
            .padding(bottom = 4.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Row(
            Modifier
              .fillMaxWidth()
              .height(42.dp)
              .selectable(
                selected = (firstDayOfWeek == FirstDayOfWeek.MONDAY),
                onClick = {
                  scope.launch {
                    settingsModel.updateFirstDayOfWeek(FirstDayOfWeek.MONDAY)
                  }
                },
                role = Role.RadioButton
              )
              .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Monday",
              Modifier.weight(1f)
            )
            RadioButton(
              selected = firstDayOfWeek == FirstDayOfWeek.MONDAY,
              onClick = null
            )
          }
        }
      }
    }
  }
}