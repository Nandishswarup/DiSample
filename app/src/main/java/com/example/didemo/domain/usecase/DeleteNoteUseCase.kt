package com.example.didemo.domain.usecase

import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use Case for deleting a note
 */
class DeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note): Result<Unit> {
        return try {
            repository.deleteNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
