package com.example.didemo.data.local.dao

import androidx.room.*
import com.example.didemo.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Note operations
 * 
 * Key Points:
 * - Returns Flow for reactive updates
 * - Suspend functions for coroutine support
 * - No DI annotations here - Room generates the implementation
 */
@Dao
interface NoteDao {
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long
    
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    
    @Query("SELECT COUNT(*) FROM notes WHERE isSynced = 0")
    fun getUnsyncedNotesCount(): Flow<Int>
    
    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>
    
    @Query("UPDATE notes SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markNotesAsSynced(ids: List<Long>)
}
