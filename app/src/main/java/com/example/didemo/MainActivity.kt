package com.example.didemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.didemo.presentation.notes.NotesScreen
import com.example.didemo.ui.theme.DiDemoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 * 
 * KEY DI CONCEPT: @AndroidEntryPoint
 * 
 * @AndroidEntryPoint annotation:
 * - Enables Hilt injection in this Activity
 * - Required for any Android component that needs injection
 * - Generates code to set up DI at runtime
 * 
 * You need @AndroidEntryPoint on:
 * - Activities
 * - Fragments
 * - Views
 * - Services
 * - BroadcastReceivers
 * 
 * Interview Question: "What does @AndroidEntryPoint do?"
 * Answer: 
 *   - Marks Android components for DI
 *   - Generates base class with injection code
 *   - Connects component to Hilt dependency graph
 *   - Must be on parent if you want DI in children (e.g., Fragment)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    /**
     * No need for ViewModelProvider factory!
     * hiltViewModel() in Composable handles it
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DiDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesScreen()
                }
            }
        }
    }
}
