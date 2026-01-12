package com.example.didemo.domain.usecase

import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use Case for getting a specific note by ID
 * 
 * Demonstrates constructor injection with suspend function
 */
class GetNoteByIdUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(id: Long): Note? {
        return repository.getNoteById(id)
    }
}
