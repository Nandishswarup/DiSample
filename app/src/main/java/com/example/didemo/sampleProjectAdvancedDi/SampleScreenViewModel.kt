package com.example.didemo.sampleProjectAdvancedDi

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SampleScreenViewModel @Inject constructor(val trackingAnalytics: TrackingAnalytics) :
    ViewModel() {
    val title = mutableStateOf<String>("this is sample screen VM")


    fun trackEvent(event: String) {
        trackingAnalytics.trackEvent(event)
    }
}