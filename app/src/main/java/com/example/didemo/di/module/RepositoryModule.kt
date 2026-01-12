package com.example.didemo.di.module

import com.example.didemo.data.repository.NotesRepositoryImpl
import com.example.didemo.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository Module - Binds repository implementation
 * 
 * KEY DI CONCEPTS REVIEW:
 * 
 * 1. Why Separate Modules?
 *    - Separation of Concerns
 *    - Easy to replace implementations
 *    - Better testability
 *    - Clear dependencies
 * 
 * 2. @Binds Benefits:
 *    - Clean and concise
 *    - Type-safe
 *    - Compile-time verification
 * 
 * 3. Repository Pattern with DI:
 *    - Domain layer defines interface (NotesRepository)
 *    - Data layer implements interface (NotesRepositoryImpl)
 *    - DI binds them together
 *    - ViewModels depend on interface, not implementation
 * 
 * Interview Question: "Explain the Repository pattern and how DI helps"
 * Answer:
 *   - Repository abstracts data sources
 *   - Domain depends on interface, not implementation
 *   - DI injects the correct implementation
 *   - Easy to swap data sources (local, remote, cache)
 *   - Easy to test (mock repository)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds NotesRepositoryImpl to NotesRepository interface
     * 
     * What happens:
     * 1. ViewModel requests NotesRepository
     * 2. Hilt sees this binding
     * 3. Hilt creates NotesRepositoryImpl
     * 4. NotesRepositoryImpl needs LocalDataSource and RemoteDataSource
     * 5. Hilt uses DataSourceModule to provide them
     * 6. Hilt injects NotesRepositoryImpl into ViewModel
     * 
     * This is the magic of DI - automatic dependency resolution!
     */
    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepositoryImpl: NotesRepositoryImpl
    ): NotesRepository
}

/**
 * DEPENDENCY GRAPH for Interview Discussion:
 * 
 * ViewModel
 *    ↓ (depends on)
 * NotesRepository (interface)
 *    ↓ (implemented by)
 * NotesRepositoryImpl
 *    ↓ (depends on)
 * ├─ LocalDataSource (interface)
 * │     ↓ (implemented by)
 * │  LocalDataSourceImpl
 * │     ↓ (depends on)
 * │  NoteDao
 * │     ↓ (depends on)
 * │  NotesDatabase
 * │
 * └─ RemoteDataSource (interface)
 *       ↓ (implemented by)
 *    FakeRemoteDataSource
 * 
 * Hilt automatically creates and manages this entire graph!
 */
