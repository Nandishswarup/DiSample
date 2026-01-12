package com.example.didemo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.didemo.data.local.dao.NoteDao
import com.example.didemo.data.local.entity.NoteEntity

/**
 * Room Database for Notes
 * 
 * Key DI Concept: 
 * - This will be provided by Hilt using @Provides in AppModule
 * - The abstract function will be called by Hilt to get the DAO
 */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    
    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}
