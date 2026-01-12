package com.example.didemo.di.module

import com.example.didemo.di.qualifier.DefaultDispatcher
import com.example.didemo.di.qualifier.IoDispatcher
import com.example.didemo.di.qualifier.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Dispatcher Module - Provides Coroutine Dispatchers
 * 
 * KEY DI CONCEPT: Providing Multiple Instances of Same Type
 * 
 * Problem:
 * - We need different CoroutineDispatchers (IO, Main, Default)
 * - They're all of type CoroutineDispatcher
 * - How does Hilt know which one to inject?
 * 
 * Solution: Custom Qualifiers!
 * 
 * Usage in code:
 * ```
 * class MyRepository @Inject constructor(
 *     @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
 *     @MainDispatcher private val mainDispatcher: CoroutineDispatcher
 * )
 * ```
 * 
 * Interview Question: "How do you provide multiple instances of the same type?"
 * Answer: Use @Qualifier annotations to distinguish them
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    
    /**
     * Provides IO Dispatcher for network/database operations
     * 
     * @IoDispatcher qualifier identifies this specific dispatcher
     * @Singleton - Dispatchers are thread-safe and reusable
     */
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
    
    /**
     * Provides Main Dispatcher for UI operations
     */
    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
    
    /**
     * Provides Default Dispatcher for CPU-intensive operations
     */
    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }
}

/**
 * Why use @Provides here instead of @Binds?
 * 
 * - Dispatchers.IO returns CoroutineDispatcher
 * - We don't have a class to bind
 * - We're returning objects from Kotlin standard library
 * - @Provides is necessary for this case
 */
