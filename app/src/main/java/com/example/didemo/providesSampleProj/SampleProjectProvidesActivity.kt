package com.example.didemo.providesSampleProj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.didemo.sampleProjectAdvancedDi.sampleScreenDemo
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SampleProjectProvidesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    providesScreenDemo()
                }
            }


        }


    }
}