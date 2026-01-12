package com.example.didemo.data.remote

import com.example.didemo.data.local.entity.NoteEntity
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Remote data source interface
 * In a real app, this would use Retrofit or similar
 * 
 * Key DI Concept: Interface for testability and flexibility
 */
interface RemoteDataSource {
    suspend fun syncNotes(unsyncedNotes: List<NoteEntity>): Result<List<Long>>
    suspend fun fetchNotes(): Result<List<NoteEntity>>
}

/**
 * Fake implementation for demonstration
 * In a real app, this would be RetrofitDataSource or similar
 * 
 * Key DI Concept: Easy to swap implementations
 * - For production: Use RetrofitDataSource
 * - For testing: Use FakeRemoteDataSource
 * - For offline mode: Use EmptyRemoteDataSource
 */
class FakeRemoteDataSource @Inject constructor() : RemoteDataSource {
    
    override suspend fun syncNotes(unsyncedNotes: List<NoteEntity>): Result<List<Long>> {
        // Simulate network delay
        delay(1000)
        
        // Simulate successful sync
        // In a real app, this would POST to a server and return synced IDs
        return Result.success(unsyncedNotes.map { it.id })
    }
    
    override suspend fun fetchNotes(): Result<List<NoteEntity>> {
        // Simulate network delay
        delay(1000)
        
        // In a real app, this would GET from a server
        // For demo, return empty list (local is source of truth)
        return Result.success(emptyList())
    }
}
