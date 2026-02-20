package com.example.didemo.demo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.didemo.demo.CounterViewModel

@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Counter screen Demo", modifier = Modifier.padding(start = 10.dp, top = 100.dp))


            Button(modifier = Modifier.padding(start = 10.dp, top = 20.dp), onClick = {
                println("Button Clicked")
                viewModel.buttonClicked(viewModel.count)
            }) {
                Text("BUTTON")
            }

        }
    }

}