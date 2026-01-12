package com.example.didemo.di.module

import android.content.Context
import androidx.room.Room
import com.example.didemo.data.local.dao.NoteDao
import com.example.didemo.data.local.database.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module - Provides Room Database and DAOs
 * 
 * KEY DI CONCEPTS:
 * 
 * 1. @Module - Tells Hilt this is a module that provides dependencies
 * 
 * 2. @InstallIn(SingletonComponent::class)
 *    - Specifies the component scope
 *    - SingletonComponent = Application lifetime
 *    - Other options: ActivityComponent, FragmentComponent, ViewModelComponent
 * 
 * 3. @Provides - Used when:
 *    - You don't own the class (e.g., Room, Retrofit)
 *    - Constructor injection isn't possible
 *    - You need to return an interface from a builder
 * 
 * 4. @Singleton - Ensures single instance
 *    - Database should be singleton (expensive to create)
 *    - One instance per app lifecycle
 * 
 * 5. @ApplicationContext - Hilt-provided qualifier
 *    - Injects application context
 *    - Prevents memory leaks (not activity context)
 * 
 * Interview Question: "Why use @Provides instead of @Inject?"
 * Answer: Use @Provides when you can't modify the class or need complex creation logic
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides Room Database instance
     * 
     * @Provides - Because Room.databaseBuilder() creates the instance
     * @Singleton - Database is expensive, create once
     * @ApplicationContext - Safe context that lives as long as the app
     */
    @Provides
    @Singleton
    fun provideNotesDatabase(
        @ApplicationContext context: Context
    ): NotesDatabase {
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For demo purposes
            .build()
    }
    
    /**
     * Provides NoteDao from Database
     * 
     * Note: This is @Singleton even though not annotated
     * Why? Because the database is singleton, so the DAO is too
     * 
     * Interview Tip: DAOs are thread-safe and can be singleton
     */
    @Provides
    @Singleton
    fun provideNoteDao(database: NotesDatabase): NoteDao {
        return database.noteDao()
    }
}
