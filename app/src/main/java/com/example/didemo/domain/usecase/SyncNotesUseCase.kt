package com.example.didemo.domain.usecase

import com.example.didemo.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use Case for syncing notes with remote source
 * Demonstrates offline-first architecture pattern
 */
class SyncNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.syncNotes()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
