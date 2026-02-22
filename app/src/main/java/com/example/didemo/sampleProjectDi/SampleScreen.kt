package com.example.didemo.sampleProjectDi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun sampleScreenDemo() {
    val viewmodel: SampleScreenViewModel = hiltViewModel()

    Column {
        Text(text = viewmodel.title.value, modifier = Modifier.clickable{
            viewmodel.trackEvent("click event")
        })
    }

}