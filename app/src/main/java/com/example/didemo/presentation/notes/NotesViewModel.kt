package com.example.didemo.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Notes Screen
 * 
 * KEY DI CONCEPTS:
 * 
 * 1. @HiltViewModel - Enables Hilt injection in ViewModel
 *    - Automatically creates ViewModelFactory
 *    - No need to manually create factory
 *    - Scoped to ViewModel lifecycle
 * 
 * 2. Constructor Injection with Multiple Dependencies
 *    - Inject all use cases via constructor
 *    - Hilt provides all dependencies automatically
 *    - Clean and testable
 * 
 * 3. @Inject Constructor
 *    - Tells Hilt how to create this ViewModel
 *    - All parameters are provided by DI graph
 * 
 * Interview Question: "How do you inject dependencies into ViewModel?"
 * Answer: Use @HiltViewModel annotation and @Inject constructor
 * 
 * Benefits:
 * - No need for ViewModelFactory boilerplate
 * - Easy to test (inject mock use cases)
 * - Follows Single Responsibility (each use case does one thing)
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncNotesUseCase: SyncNotesUseCase
) : ViewModel() {
    
    // UI State using StateFlow
    private val _notesState = MutableStateFlow<NotesState>(NotesState.Loading)
    val notesState: StateFlow<NotesState> = _notesState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    init {
        loadNotes()
    }
    
    /**
     * Load notes from repository
     * Demonstrates Flow collection in ViewModel
     */
    private fun loadNotes() {
        viewModelScope.launch {
            getAllNotesUseCase()
                .catch { exception ->
                    _notesState.value = NotesState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { notes ->
                    _notesState.value = if (notes.isEmpty()) {
                        NotesState.Empty
                    } else {
                        NotesState.Success(notes)
                    }
                }
        }
    }
    
    /**
     * Add a new note
     */
    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            addNoteUseCase(title, content)
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowMessage("Note added successfully"))
                }
                .onFailure { exception ->
                    _uiEvent.emit(
                        UiEvent.ShowMessage(exception.message ?: "Failed to add note")
                    )
                }
        }
    }
    
    /**
     * Update an existing note
     */
    fun updateNote(note: Note, title: String, content: String) {
        viewModelScope.launch {
            updateNoteUseCase(note, title, content)
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowMessage("Note updated successfully"))
                }
                .onFailure { exception ->
                    _uiEvent.emit(
                        UiEvent.ShowMessage(exception.message ?: "Failed to update note")
                    )
                }
        }
    }
    
    /**
     * Delete a note
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowMessage("Note deleted"))
                }
                .onFailure { exception ->
                    _uiEvent.emit(
                        UiEvent.ShowMessage(exception.message ?: "Failed to delete note")
                    )
                }
        }
    }
    
    /**
     * Sync notes with remote
     * Demonstrates offline-first pattern
     */
    fun syncNotes() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowMessage("Syncing notes..."))
            syncNotesUseCase()
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowMessage("Sync completed"))
                }
                .onFailure { exception ->
                    _uiEvent.emit(
                        UiEvent.ShowMessage(exception.message ?: "Sync failed")
                    )
                }
        }
    }
}

/**
 * UI State sealed class
 * Represents different states of the notes screen
 */
sealed class NotesState {
    object Loading : NotesState()
    object Empty : NotesState()
    data class Success(val notes: List<Note>) : NotesState()
    data class Error(val message: String) : NotesState()
}

/**
 * UI Event sealed class
 * Represents one-time events (like showing a snackbar)
 */
sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
}
