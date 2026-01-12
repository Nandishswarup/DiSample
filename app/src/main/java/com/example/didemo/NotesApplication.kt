package com.example.didemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Notes app
 * 
 * KEY DI CONCEPT: @HiltAndroidApp
 * 
 * @HiltAndroidApp annotation:
 * - REQUIRED for Hilt to work
 * - Must be on Application class
 * - Triggers Hilt code generation
 * - Creates the application-level dependency container
 * - Sets up the SingletonComponent
 * 
 * What happens:
 * 1. Hilt generates Hilt_NotesApplication class
 * 2. This generated class extends NotesApplication
 * 3. Hilt sets up the DI container when app starts
 * 4. All @Singleton dependencies are created here
 * 
 * Interview Question: "What's the first step to set up Hilt?"
 * Answer: 
 *   1. Add @HiltAndroidApp to Application class
 *   2. Register it in AndroidManifest.xml
 *   3. Add @AndroidEntryPoint to components that need injection
 * 
 * Component Hierarchy:
 * SingletonComponent (Application)
 *    ↓
 * ActivityRetainedComponent
 *    ↓
 * ViewModelComponent (ViewModel)
 *    ↓
 * ActivityComponent (Activity)
 *    ↓
 * FragmentComponent (Fragment)
 *    ↓
 * ViewComponent (View)
 */
@HiltAndroidApp
class NotesApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Hilt automatically sets up DI here
        // No manual initialization needed!
        
        // In a real app, you might initialize other things here:
        // - Timber for logging
        // - LeakCanary for memory leak detection
        // - Firebase
        // - Crash reporting
    }
}

/**
 * DI INTERVIEW TALKING POINTS:
 * 
 * 1. Scopes:
 *    - @Singleton: Lives as long as the application
 *    - @ActivityRetainedScoped: Survives configuration changes
 *    - @ViewModelScoped: Lives as long as ViewModel
 *    - @ActivityScoped: Lives as long as Activity
 * 
 * 2. Why Hilt over Manual DI?
 *    - Less boilerplate
 *    - Compile-time safety
 *    - Android lifecycle awareness
 *    - Built-in support for ViewModels
 *    - Standardized approach
 * 
 * 3. Why Hilt over Dagger?
 *    - Hilt is built on Dagger
 *    - Reduces Dagger boilerplate
 *    - Predefined components for Android
 *    - Better ViewModel support
 *    - Easier to learn and use
 * 
 * 4. Testing Benefits:
 *    - Easy to replace implementations
 *    - Hilt provides @HiltAndroidTest
 *    - Can swap modules for testing
 *    - Mock dependencies easily
 */
