package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SearchBarViewModel : ViewModel() {
  var searchBarValue = mutableStateOf("")
}