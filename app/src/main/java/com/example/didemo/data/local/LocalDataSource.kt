package com.example.didemo.data.local

import com.example.didemo.data.local.dao.NoteDao
import com.example.didemo.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source abstraction
 * Wraps Room DAO operations
 * 
 * Key DI Concepts:
 * 1. Constructor Injection - DAO injected via constructor
 * 2. Interface programming - We'll create an interface for this too
 * 3. Single source of truth - All local operations go through this
 */
interface LocalDataSource {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: Long): NoteEntity?
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun deleteAllNotes()
    fun getUnsyncedNotesCount(): Flow<Int>
    suspend fun getUnsyncedNotes(): List<NoteEntity>
    suspend fun markNotesAsSynced(ids: List<Long>)
}

/**
 * Implementation of LocalDataSource
 * This will be bound to the interface using @Binds in DI module
 */
class LocalDataSourceImpl @Inject constructor(
    private val noteDao: NoteDao
) : LocalDataSource {
    
    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }
    
    override suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.getNoteById(id)
    }
    
    override suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }
    
    override suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }
    
    override suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }
    
    override suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
    
    override fun getUnsyncedNotesCount(): Flow<Int> {
        return noteDao.getUnsyncedNotesCount()
    }
    
    override suspend fun getUnsyncedNotes(): List<NoteEntity> {
        return noteDao.getUnsyncedNotes()
    }
    
    override suspend fun markNotesAsSynced(ids: List<Long>) {
        noteDao.markNotesAsSynced(ids)
    }
}
