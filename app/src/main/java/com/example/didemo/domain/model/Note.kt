package com.example.didemo.domain.model

import java.time.LocalDateTime

/**
 * Domain model for Note
 * This is the pure business logic model without any framework dependencies
 */
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isSynced: Boolean = false
) {
    companion object {
        fun create(title: String, content: String): Note {
            val now = LocalDateTime.now()
            return Note(
                title = title,
                content = content,
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
        }
    }
    
    fun update(title: String? = null, content: String? = null): Note {
        return copy(
            title = title ?: this.title,
            content = content ?: this.content,
            updatedAt = LocalDateTime.now(),
            isSynced = false
        )
    }
}
