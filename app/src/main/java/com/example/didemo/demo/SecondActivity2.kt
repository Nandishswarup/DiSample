package com.example.didemo.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.didemo.demo.ui.CounterScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: CounterViewModel by viewModels()

        setContent {
            MaterialTheme {
                Surface {
                    CounterScreen(viewModel)
                }
            }
        }
    }
}