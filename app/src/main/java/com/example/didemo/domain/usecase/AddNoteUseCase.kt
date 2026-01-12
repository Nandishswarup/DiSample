package com.example.didemo.domain.usecase

import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use Case for adding a new note
 * 
 * Demonstrates business logic in use case layer
 */
class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(title: String, content: String): Result<Long> {
        return try {
            // Validation logic - business rules
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Title cannot be empty"))
            }
            
            val note = Note.create(title.trim(), content.trim())
            val id = repository.insertNote(note)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
