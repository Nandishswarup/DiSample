package com.example.didemo.providesSampleProj

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProvidesSampleViewModel @Inject constructor(val iLogger: InterfaceLogger) :
    ViewModel() {
        val title = mutableStateOf<String>("this is sample screen VM")


    fun logMessage(event: String) {
        iLogger.logMessage(event)
    }
}