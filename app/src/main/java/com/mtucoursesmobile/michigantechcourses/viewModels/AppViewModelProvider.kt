package com.mtucoursesmobile.michigantechcourses.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mtucoursesmobile.michigantechcourses.AppSetup

fun CreationExtras.appViewModelProvider(): AppSetup =
  (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AppSetup)