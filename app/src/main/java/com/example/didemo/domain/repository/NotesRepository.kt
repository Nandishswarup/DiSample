package com.example.didemo.domain.repository

import com.example.didemo.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface in the domain layer
 * This defines the contract without implementation details
 * 
 * Key DI Concept: Programming to interfaces, not implementations
 * This allows us to swap implementations and test easily
 */
interface NotesRepository {
    
    /**
     * Get all notes as a Flow for reactive updates
     */
    fun getAllNotes(): Flow<List<Note>>
    
    /**
     * Get a specific note by ID
     */
    suspend fun getNoteById(id: Long): Note?
    
    /**
     * Insert a new note
     * @return the ID of the inserted note
     */
    suspend fun insertNote(note: Note): Long
    
    /**
     * Update an existing note
     */
    suspend fun updateNote(note: Note)
    
    /**
     * Delete a note
     */
    suspend fun deleteNote(note: Note)
    
    /**
     * Delete all notes
     */
    suspend fun deleteAllNotes()
    
    /**
     * Sync notes with remote data source
     * This demonstrates offline-first architecture
     */
    suspend fun syncNotes()
    
    /**
     * Get unsynced notes count
     */
    fun getUnsyncedNotesCount(): Flow<Int>
}
