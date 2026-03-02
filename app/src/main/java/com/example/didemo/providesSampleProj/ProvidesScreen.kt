package com.example.didemo.providesSampleProj

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.didemo.sampleProjectAdvancedDi.SampleScreenViewModel

@Composable
fun providesScreenDemo() {
    val viewmodel: ProvidesSampleViewModel = hiltViewModel()

    Column {
        Text(text = viewmodel.title.value, modifier = Modifier.clickable{
            viewmodel.logMessage("click action")
        })
    }

}