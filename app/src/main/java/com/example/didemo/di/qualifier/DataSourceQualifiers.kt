package com.example.didemo.di.qualifier

import javax.inject.Qualifier

/**
 * Custom Qualifiers for Dependency Injection
 * 
 * KEY DI CONCEPT: Qualifiers
 * 
 * Problem: What if we have multiple implementations of the same interface?
 * Solution: Use @Qualifier annotations to distinguish between them
 * 
 * Example Interview Question:
 * "How would you provide two different implementations of the same interface?"
 * 
 * Answer: Use custom qualifiers with @Qualifier annotation
 * 
 * Use Cases:
 * - LocalDataSource vs RemoteDataSource
 * - ProductionApi vs MockApi
 * - DefaultDispatcher vs IoDispatcher
 */

/**
 * Qualifier for Local Data Source
 * Used to inject LocalDataSource implementation
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSourceQualifier

/**
 * Qualifier for Remote Data Source
 * Used to inject RemoteDataSource implementation
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSourceQualifier

/**
 * Additional qualifiers for Coroutine Dispatchers
 * Demonstrates how to provide different instances of the same type
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
