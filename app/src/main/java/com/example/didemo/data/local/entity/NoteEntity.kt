package com.example.didemo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.didemo.domain.model.Note
import java.time.LocalDateTime

/**
 * Room entity for Note
 * This is the data layer representation with Room annotations
 * 
 * Key DI Concept: Separation of concerns
 * Domain model is separate from database entity
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: String, // Stored as ISO-8601 string
    val updatedAt: String, // Stored as ISO-8601 string
    val isSynced: Boolean = false
)

/**
 * Mapper functions to convert between domain model and entity
 * This keeps the layers independent
 */
fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        isSynced = isSynced
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        isSynced = isSynced
    )
}
