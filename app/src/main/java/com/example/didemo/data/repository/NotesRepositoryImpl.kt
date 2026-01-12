package com.example.didemo.data.repository

import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.local.entity.toDomain
import com.example.didemo.data.local.entity.toEntity
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.di.qualifier.LocalDataSourceQualifier
import com.example.didemo.di.qualifier.RemoteDataSourceQualifier
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotesRepository
 * 
 * KEY DI CONCEPTS DEMONSTRATED:
 * 
 * 1. @Singleton - Single instance across the app
 *    - Repository should be singleton to maintain consistency
 *    - All ViewModels share the same instance
 * 
 * 2. Constructor Injection with @Inject
 *    - Hilt automatically provides dependencies
 *    - No manual instantiation needed
 * 
 * 3. Custom Qualifiers (@LocalDataSourceQualifier, @RemoteDataSourceQualifier)
 *    - Distinguishes between multiple implementations of same interface
 *    - If we had multiple LocalDataSource implementations, qualifiers help
 *    - Interview question: "How do you provide different implementations of same type?"
 * 
 * 4. Interface Implementation
 *    - Implements domain's NotesRepository interface
 *    - Will be bound using @Binds in DI module
 * 
 * 5. Dependency Inversion Principle
 *    - High-level module (domain) doesn't depend on low-level module (data)
 *    - Both depend on abstraction (NotesRepository interface)
 */
@Singleton
class NotesRepositoryImpl @Inject constructor(
    @LocalDataSourceQualifier private val localDataSource: LocalDataSource,
    @RemoteDataSourceQualifier private val remoteDataSource: RemoteDataSource
) : NotesRepository {
    
    /**
     * Offline-first approach: Local database is the source of truth
     * Returns Flow for reactive updates
     */
    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getNoteById(id: Long): Note? {
        return localDataSource.getNoteById(id)?.toDomain()
    }
    
    override suspend fun insertNote(note: Note): Long {
        return localDataSource.insertNote(note.toEntity())
    }
    
    override suspend fun updateNote(note: Note) {
        localDataSource.updateNote(note.toEntity())
    }
    
    override suspend fun deleteNote(note: Note) {
        localDataSource.deleteNote(note.toEntity())
    }
    
    override suspend fun deleteAllNotes() {
        localDataSource.deleteAllNotes()
    }
    
    /**
     * Demonstrates offline-first sync pattern
     * 1. Get unsynced notes from local
     * 2. Send to remote
     * 3. Mark as synced in local
     */
    override suspend fun syncNotes() {
        val unsyncedNotes = localDataSource.getUnsyncedNotes()
        
        if (unsyncedNotes.isEmpty()) return
        
        remoteDataSource.syncNotes(unsyncedNotes)
            .onSuccess { syncedIds ->
                localDataSource.markNotesAsSynced(syncedIds)
            }
            .onFailure { 
                // In a real app, handle sync failure
                // Maybe retry, show error, etc.
            }
    }
    
    override fun getUnsyncedNotesCount(): Flow<Int> {
        return localDataSource.getUnsyncedNotesCount()
    }
}
