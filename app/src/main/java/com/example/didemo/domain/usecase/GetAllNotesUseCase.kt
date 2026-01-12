package com.example.didemo.domain.usecase

import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case for getting all notes
 * 
 * Key DI Concepts demonstrated:
 * 1. Constructor Injection - Dependencies injected via constructor
 * 2. Single Responsibility - Each use case has one specific purpose
 * 3. Testability - Easy to mock repository for testing
 */
class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Invoke operator allows us to call the use case like a function
     * Example: getAllNotesUseCase() instead of getAllNotesUseCase.execute()
     */
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}
