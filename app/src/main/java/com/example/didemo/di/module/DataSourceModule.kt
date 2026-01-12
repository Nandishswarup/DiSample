package com.example.didemo.di.module

import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.local.LocalDataSourceImpl
import com.example.didemo.data.remote.FakeRemoteDataSource
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.di.qualifier.LocalDataSourceQualifier
import com.example.didemo.di.qualifier.RemoteDataSourceQualifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Data Source Module - Binds implementations to interfaces
 * 
 * KEY DI CONCEPTS:
 * 
 * 1. @Binds vs @Provides:
 *    - @Binds: For simple interface -> implementation binding
 *    - @Binds is more efficient (generates less code)
 *    - @Provides: For complex creation logic
 * 
 * 2. Abstract Class with @Binds:
 *    - Module must be abstract when using @Binds
 *    - Methods must be abstract
 *    - No method body needed
 * 
 * 3. Custom Qualifiers:
 *    - @LocalDataSourceQualifier and @RemoteDataSourceQualifier
 *    - Helps Hilt distinguish between multiple DataSource types
 * 
 * Interview Question: "When to use @Binds vs @Provides?"
 * Answer:
 *   @Binds - Interface to implementation (simpler, more efficient)
 *   @Provides - Complex object creation, builders, third-party libs
 * 
 * Example:
 *   @Binds -> LocalDataSource implementation
 *   @Provides -> Retrofit instance (complex builder)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    
    /**
     * Binds LocalDataSourceImpl to LocalDataSource interface
     * 
     * Key Points:
     * - Abstract function (no body)
     * - @Binds annotation
     * - Return type is the interface
     * - Parameter is the implementation
     * - @LocalDataSourceQualifier to identify this specific binding
     * 
     * What Hilt does:
     * - When someone requests LocalDataSource with @LocalDataSourceQualifier
     * - Hilt provides LocalDataSourceImpl
     * - LocalDataSourceImpl's constructor dependencies are auto-injected
     */
    @Binds
    @Singleton
    @LocalDataSourceQualifier
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): LocalDataSource
    
    /**
     * Binds FakeRemoteDataSource to RemoteDataSource interface
     * 
     * In a real app, you might have:
     * - @ProductionQualifier -> RetrofitDataSource
     * - @DevelopmentQualifier -> FakeRemoteDataSource
     * - @TestingQualifier -> MockRemoteDataSource
     */
    @Binds
    @Singleton
    @RemoteDataSourceQualifier
    abstract fun bindRemoteDataSource(
        fakeRemoteDataSource: FakeRemoteDataSource
    ): RemoteDataSource
}

/**
 * COMPARISON: What if we used @Provides instead?
 * 
 * @Provides
 * fun provideLocalDataSource(impl: LocalDataSourceImpl): LocalDataSource = impl
 * 
 * This works, but:
 * - More boilerplate
 * - Generates more code
 * - Less efficient
 * 
 * Rule of thumb: Use @Binds when possible, @Provides when necessary
 */
