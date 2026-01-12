package com.example.didemo.domain.usecase

import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use Case for updating an existing note
 */
class UpdateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note, title: String? = null, content: String? = null): Result<Unit> {
        return try {
            if (title?.isBlank() == true) {
                return Result.failure(IllegalArgumentException("Title cannot be empty"))
            }
            
            val updatedNote = note.update(title?.trim(), content?.trim())
            repository.updateNote(updatedNote)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
