# 🔄 WITHOUT Dependency Injection - Code Comparison

This document shows exactly what the code would look like **WITHOUT** using Dependency Injection, highlighting the problems and complexity that DI solves.

---

## 📋 Table of Contents

1. [NotesRepository Comparison](#notesrepository-comparison)
2. [NotesRepositoryImpl Comparison](#notesrepositoryimpl-comparison)
3. [ViewModel Comparison](#viewmodel-comparison)
4. [Activity Comparison](#activity-comparison)
5. [Application Class Comparison](#application-class-comparison)
6. [Testing Comparison](#testing-comparison)
7. [Problems Summary](#problems-summary)
8. [Benefits of DI](#benefits-of-di)

---

## NotesRepository Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
package com.example.didemo.domain.repository

import com.example.didemo.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Clean interface - no implementation details
 * No dependencies on Android framework
 * Easy to mock for testing
 */
interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteAllNotes()
    suspend fun syncNotes()
    fun getUnsyncedNotesCount(): Flow<Int>
}
```

**Benefits:**
- ✅ Pure interface, no implementation
- ✅ No dependencies
- ✅ Easy to mock
- ✅ Framework-independent

---

### ❌ WITHOUT Dependency Injection (Alternative)

```kotlin
package com.example.didemo.domain.repository

import android.content.Context
import com.example.didemo.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * WITHOUT DI - Interface needs Context
 * This is BAD because:
 * 1. Domain layer now depends on Android framework
 * 2. Can't test without Android context
 * 3. Breaks Clean Architecture
 */
interface NotesRepository {
    fun getAllNotes(context: Context): Flow<List<Note>>
    suspend fun getNoteById(context: Context, id: Long): Note?
    suspend fun insertNote(context: Context, note: Note): Long
    suspend fun updateNote(context: Context, note: Note)
    suspend fun deleteNote(context: Context, note: Note)
    suspend fun deleteAllNotes(context: Context)
    suspend fun syncNotes(context: Context)
    fun getUnsyncedNotesCount(context: Context): Flow<Int>
}

// OR using Singleton pattern (also problematic)
interface NotesRepository {
    companion object {
        private var instance: NotesRepository? = null
        
        fun getInstance(context: Context): NotesRepository {
            if (instance == null) {
                instance = NotesRepositoryImpl(context)
            }
            return instance!!
        }
    }
    
    fun getAllNotes(): Flow<List<Note>>
    // ... other methods
}
```

**Problems:**
- ❌ Context needed everywhere
- ❌ Singleton pattern is anti-pattern
- ❌ Hard to test
- ❌ Domain layer polluted with Android
- ❌ Not thread-safe
- ❌ Memory leak risk

---

## NotesRepositoryImpl Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
package com.example.didemo.data.repository

import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.di.qualifier.LocalDataSourceQualifier
import com.example.didemo.di.qualifier.RemoteDataSourceQualifier
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WITH DI - Clean and testable
 * Dependencies injected via constructor
 * Hilt manages lifecycle and creation
 */
@Singleton
class NotesRepositoryImpl @Inject constructor(
    @LocalDataSourceQualifier private val localDataSource: LocalDataSource,
    @RemoteDataSourceQualifier private val remoteDataSource: RemoteDataSource
) : NotesRepository {
    
    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        return localDataSource.insertNote(note.toEntity())
    }
    
    // ... other clean methods
}
```

**Benefits:**
- ✅ Dependencies injected automatically
- ✅ @Singleton ensures single instance
- ✅ No manual object creation
- ✅ Easy to test (inject mocks)
- ✅ No context needed
- ✅ Thread-safe (Hilt guarantees)
- ✅ Clean and readable

---

### ❌ WITHOUT Dependency Injection (Alternative)

#### Option 1: Context Everywhere (TERRIBLE)

```kotlin
package com.example.didemo.data.repository

import android.content.Context
import androidx.room.Room
import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.local.LocalDataSourceImpl
import com.example.didemo.data.local.database.NotesDatabase
import com.example.didemo.data.remote.FakeRemoteDataSource
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * WITHOUT DI - OPTION 1: Pass Context everywhere
 * This is TERRIBLE because:
 * 1. Context passed through every layer
 * 2. Manual object creation everywhere
 * 3. No lifecycle management
 * 4. Memory leaks possible
 * 5. Hard to test
 */
class NotesRepositoryImpl(
    private val context: Context
) : NotesRepository {
    
    // Have to create everything manually!
    private val database: NotesDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext, // Risk: what if wrong context?
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }
    
    private val noteDao by lazy {
        database.noteDao()
    }
    
    private val localDataSource: LocalDataSource by lazy {
        LocalDataSourceImpl(noteDao)
    }
    
    private val remoteDataSource: RemoteDataSource by lazy {
        FakeRemoteDataSource()
    }
    
    override fun getAllNotes(): Flow<List<Note>> {
        // Same logic but with manual dependency management
        return localDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        return localDataSource.insertNote(note.toEntity())
    }
    
    override suspend fun syncNotes() {
        val unsyncedNotes = localDataSource.getUnsyncedNotes()
        
        if (unsyncedNotes.isEmpty()) return
        
        remoteDataSource.syncNotes(unsyncedNotes)
            .onSuccess { syncedIds ->
                localDataSource.markNotesAsSynced(syncedIds)
            }
    }
    
    // ... other methods
}
```

**Problems with Option 1:**
- ❌ Context dependency everywhere
- ❌ Manual lazy initialization
- ❌ No guarantee of single database instance
- ❌ Memory leak risk (wrong context)
- ❌ Hard to swap implementations
- ❌ Testing requires real Context
- ❌ No compile-time safety

---

#### Option 2: Singleton Pattern (BAD)

```kotlin
package com.example.didemo.data.repository

import android.content.Context
import androidx.room.Room
import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.local.LocalDataSourceImpl
import com.example.didemo.data.local.database.NotesDatabase
import com.example.didemo.data.remote.FakeRemoteDataSource
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * WITHOUT DI - OPTION 2: Singleton Pattern
 * This is BAD because:
 * 1. Global mutable state
 * 2. Hard to test (can't reset singleton)
 * 3. Not thread-safe (without careful synchronization)
 * 4. Hidden dependencies
 * 5. Initialization order issues
 */
class NotesRepositoryImpl private constructor(
    context: Context
) : NotesRepository {
    
    private val database: NotesDatabase
    private val localDataSource: LocalDataSource
    private val remoteDataSource: RemoteDataSource
    
    init {
        // All initialization in constructor
        // What if this fails? Can't retry!
        database = Room.databaseBuilder(
            context.applicationContext,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
        
        val noteDao = database.noteDao()
        localDataSource = LocalDataSourceImpl(noteDao)
        remoteDataSource = FakeRemoteDataSource()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: NotesRepositoryImpl? = null
        
        /**
         * Double-checked locking singleton
         * Complex and error-prone!
         */
        fun getInstance(context: Context): NotesRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotesRepositoryImpl(context.applicationContext)
                    .also { INSTANCE = it }
            }
        }
        
        /**
         * For testing - but this is a hack!
         * In real app, hard to reset between tests
         */
        @VisibleForTesting
        fun resetInstance() {
            INSTANCE = null
        }
    }
    
    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        return localDataSource.insertNote(note.toEntity())
    }
    
    override suspend fun syncNotes() {
        val unsyncedNotes = localDataSource.getUnsyncedNotes()
        
        if (unsyncedNotes.isEmpty()) return
        
        remoteDataSource.syncNotes(unsyncedNotes)
            .onSuccess { syncedIds ->
                localDataSource.markNotesAsSynced(syncedIds)
            }
    }
    
    // ... other methods
}
```

**Problems with Option 2:**
- ❌ Global mutable state (INSTANCE)
- ❌ Complex thread-safety code (@Volatile, synchronized)
- ❌ Hard to test (need to reset singleton)
- ❌ Hidden dependency on Context
- ❌ Can't easily swap implementations
- ❌ Initialization can't fail gracefully
- ❌ Memory leak if Context is Activity

---

#### Option 3: Service Locator (ALSO BAD)

```kotlin
package com.example.didemo.data.repository

import com.example.didemo.data.local.LocalDataSource
import com.example.didemo.data.remote.RemoteDataSource
import com.example.didemo.domain.model.Note
import com.example.didemo.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * WITHOUT DI - OPTION 3: Service Locator Pattern
 * Better than Singleton but still problematic
 */
class NotesRepositoryImpl(
    // Dependencies from ServiceLocator
    private val localDataSource: LocalDataSource = ServiceLocator.getLocalDataSource(),
    private val remoteDataSource: RemoteDataSource = ServiceLocator.getRemoteDataSource()
) : NotesRepository {
    
    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        return localDataSource.insertNote(note.toEntity())
    }
    
    override suspend fun syncNotes() {
        val unsyncedNotes = localDataSource.getUnsyncedNotes()
        
        if (unsyncedNotes.isEmpty()) return
        
        remoteDataSource.syncNotes(unsyncedNotes)
            .onSuccess { syncedIds ->
                localDataSource.markNotesAsSynced(syncedIds)
            }
    }
    
    // ... other methods
}

/**
 * Service Locator - Manual dependency container
 * This is what Hilt does automatically!
 */
object ServiceLocator {
    private var localDataSource: LocalDataSource? = null
    private var remoteDataSource: RemoteDataSource? = null
    private var database: NotesDatabase? = null
    
    fun initialize(context: Context) {
        // Must be called before using any dependencies!
        database = Room.databaseBuilder(
            context.applicationContext,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
        
        localDataSource = LocalDataSourceImpl(database!!.noteDao())
        remoteDataSource = FakeRemoteDataSource()
    }
    
    fun getLocalDataSource(): LocalDataSource {
        return localDataSource ?: throw IllegalStateException(
            "ServiceLocator not initialized! Call initialize() first."
        )
    }
    
    fun getRemoteDataSource(): RemoteDataSource {
        return remoteDataSource ?: throw IllegalStateException(
            "ServiceLocator not initialized! Call initialize() first."
        )
    }
    
    fun getDatabase(): NotesDatabase {
        return database ?: throw IllegalStateException(
            "ServiceLocator not initialized! Call initialize() first."
        )
    }
    
    // For testing
    fun resetForTesting() {
        localDataSource = null
        remoteDataSource = null
        database = null
    }
}
```

**Problems with Option 3:**
- ❌ Manual dependency management
- ❌ Runtime errors (not compile-time)
- ❌ Must remember to call initialize()
- ❌ Hidden dependencies (ServiceLocator is global)
- ❌ Hard to see what depends on what
- ❌ Testing requires manual reset
- ❌ No scoping support

---

## ViewModel Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncNotesUseCase: SyncNotesUseCase
) : ViewModel() {
    // Clean implementation
}
```

**Benefits:**
- ✅ Automatic injection
- ✅ No factory needed
- ✅ Clean and simple
- ✅ Easy to test

---

### ❌ WITHOUT Dependency Injection (Alternative)

```kotlin
/**
 * WITHOUT DI - Manual ViewModel creation
 * Need to create ViewModelFactory!
 */
class NotesViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncNotesUseCase: SyncNotesUseCase
) : ViewModel() {
    // Same implementation
}

/**
 * ViewModelFactory - BOILERPLATE!
 * Have to create this manually for EVERY ViewModel
 */
class NotesViewModelFactory(
    private val context: Context // Need context to create dependencies!
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            // Create entire dependency tree manually!
            val database = Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                NotesDatabase.DATABASE_NAME
            ).build()
            
            val noteDao = database.noteDao()
            val localDataSource = LocalDataSourceImpl(noteDao)
            val remoteDataSource = FakeRemoteDataSource()
            val repository = NotesRepositoryImpl(localDataSource, remoteDataSource)
            
            val getAllNotesUseCase = GetAllNotesUseCase(repository)
            val addNoteUseCase = AddNoteUseCase(repository)
            val updateNoteUseCase = UpdateNoteUseCase(repository)
            val deleteNoteUseCase = DeleteNoteUseCase(repository)
            val syncNotesUseCase = SyncNotesUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(
                getAllNotesUseCase,
                addNoteUseCase,
                updateNoteUseCase,
                deleteNoteUseCase,
                syncNotesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// OR using ServiceLocator (still bad)
class NotesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val repository = ServiceLocator.getRepository()
            
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(
                GetAllNotesUseCase(repository),
                AddNoteUseCase(repository),
                UpdateNoteUseCase(repository),
                DeleteNoteUseCase(repository),
                SyncNotesUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

**Problems:**
- ❌ Must create ViewModelFactory for each ViewModel
- ❌ Lots of boilerplate
- ❌ Manual dependency creation
- ❌ Error-prone
- ❌ Hard to maintain
- ❌ Context dependency

---

## Activity Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DiDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesScreen() // hiltViewModel() handles everything!
                }
            }
        }
    }
}
```

**Benefits:**
- ✅ Clean and simple
- ✅ @AndroidEntryPoint handles injection
- ✅ hiltViewModel() gets ViewModel automatically
- ✅ No factory needed

---

### ❌ WITHOUT Dependency Injection (Alternative)

```kotlin
/**
 * WITHOUT DI - Manual setup in Activity
 */
class MainActivity : ComponentActivity() {
    
    // Option 1: Create factory in Activity
    private lateinit var viewModelFactory: NotesViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Must create factory manually!
        viewModelFactory = NotesViewModelFactory(applicationContext)
        
        setContent {
            DiDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Must pass factory to get ViewModel
                    NotesScreen(
                        viewModel = viewModel(factory = viewModelFactory)
                    )
                }
            }
        }
    }
}

// OR using ServiceLocator (Option 2)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Must initialize ServiceLocator!
        ServiceLocator.initialize(applicationContext)
        
        setContent {
            DiDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesScreen(
                        viewModel = viewModel(factory = NotesViewModelFactory())
                    )
                }
            }
        }
    }
}
```

**Problems:**
- ❌ Must create factory in every Activity
- ❌ Must pass factory to viewModel()
- ❌ ServiceLocator needs initialization
- ❌ More code in Activity
- ❌ Easy to forget initialization

---

## Application Class Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
@HiltAndroidApp
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Hilt handles everything!
    }
}
```

**Benefits:**
- ✅ One annotation
- ✅ Everything automatic
- ✅ Clean and simple

---

### ❌ WITHOUT Dependency Injection (Alternative)

```kotlin
/**
 * WITHOUT DI - Manual initialization
 */
class NotesApplication : Application() {
    
    // Option 1: Singleton pattern
    companion object {
        private var database: NotesDatabase? = null
        
        fun getDatabase(context: Context): NotesDatabase {
            return database ?: synchronized(this) {
                database ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    NotesDatabase.DATABASE_NAME
                ).build().also { database = it }
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize database
        getDatabase(this)
    }
}

// OR Option 2: ServiceLocator
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Must initialize ServiceLocator
        ServiceLocator.initialize(this)
    }
}

// OR Option 3: Global variables (WORST!)
class NotesApplication : Application() {
    companion object {
        lateinit var database: NotesDatabase
        lateinit var repository: NotesRepository
        
        fun initialize(context: Context) {
            database = Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                NotesDatabase.DATABASE_NAME
            ).build()
            
            val localDataSource = LocalDataSourceImpl(database.noteDao())
            val remoteDataSource = FakeRemoteDataSource()
            repository = NotesRepositoryImpl(localDataSource, remoteDataSource)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        initialize(this)
    }
}

// Then access from anywhere (BAD!)
fun someFunction() {
    val notes = NotesApplication.repository.getAllNotes()
}
```

**Problems:**
- ❌ Manual initialization
- ❌ Global state
- ❌ Hard to test
- ❌ Initialization order matters
- ❌ lateinit can crash if not initialized

---

## Testing Comparison

### ✅ WITH Dependency Injection (Current)

```kotlin
/**
 * WITH DI - Easy to test!
 */
class NotesViewModelTest {
    
    @Test
    fun `add note should call use case`() = runTest {
        // Easy to create mocks
        val mockAddNoteUseCase = mock<AddNoteUseCase> {
            onBlocking { invoke(any(), any()) } doReturn Result.success(1L)
        }
        val mockGetAllNotesUseCase = mock<GetAllNotesUseCase>()
        
        // Easy to create ViewModel with mocks
        val viewModel = NotesViewModel(
            getAllNotesUseCase = mockGetAllNotesUseCase,
            addNoteUseCase = mockAddNoteUseCase,
            updateNoteUseCase = mock(),
            deleteNoteUseCase = mock(),
            syncNotesUseCase = mock()
        )
        
        // Test
        viewModel.addNote("Test", "Content")
        
        // Verify
        verify(mockAddNoteUseCase).invoke("Test", "Content")
    }
}

/**
 * WITH Hilt - Integration testing
 */
@HiltAndroidTest
class NotesRepositoryIntegrationTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: NotesRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun `insert note should return id`() = runTest {
        val note = Note.create("Test", "Content")
        val id = repository.insertNote(note)
        assert(id > 0)
    }
}
```

**Benefits:**
- ✅ Easy to create mocks
- ✅ Easy to inject mocks
- ✅ Hilt provides testing support
- ✅ Clean test code

---

### ❌ WITHOUT Dependency Injection (Alternative)

```kotlin
/**
 * WITHOUT DI - Hard to test!
 */
class NotesViewModelTest {
    
    @Test
    fun `add note should call use case`() = runTest {
        // Problem: How do we create ViewModel?
        // Need real Context for Repository!
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Problem: Creates real database!
        val factory = NotesViewModelFactory(context)
        val viewModel = factory.create(NotesViewModel::class.java)
        
        // Problem: This calls real database, not mocks!
        viewModel.addNote("Test", "Content")
        
        // Can't verify use case was called (no mocks!)
    }
}

// OR with ServiceLocator (slightly better but still bad)
class NotesViewModelTest {
    
    @Before
    fun setup() {
        // Must manually set up mocks in ServiceLocator
        ServiceLocator.setRepositoryForTesting(mockRepository)
    }
    
    @After
    fun teardown() {
        // Must manually reset
        ServiceLocator.resetForTesting()
    }
    
    @Test
    fun `add note should call use case`() = runTest {
        // Still creating ViewModel is awkward
        val viewModel = NotesViewModelFactory().create(NotesViewModel::class.java)
        
        viewModel.addNote("Test", "Content")
        
        // Verify on mock repository
        verify(mockRepository).insertNote(any())
    }
}

// Repository test WITHOUT DI (very hard!)
class NotesRepositoryImplTest {
    
    private lateinit var context: Context
    private lateinit var database: NotesDatabase
    private lateinit var repository: NotesRepositoryImpl
    
    @Before
    fun setup() {
        // Need real Android context
        context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Create in-memory database
        database = Room.inMemoryDatabaseBuilder(
            context,
            NotesDatabase::class.java
        ).build()
        
        // Problem: Can't easily inject mocks!
        // Repository creates its own dependencies
        repository = NotesRepositoryImpl(context)
        
        // Or with modified constructor:
        // repository = NotesRepositoryImpl(
        //     LocalDataSourceImpl(database.noteDao()),
        //     FakeRemoteDataSource()
        // )
        // But this requires changing production code for testing!
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun `insert note should work`() = runTest {
        val note = Note.create("Test", "Content")
        val id = repository.insertNote(note)
        assert(id > 0)
    }
}
```

**Problems:**
- ❌ Need real Android context
- ❌ Creates real dependencies
- ❌ Hard to use mocks
- ❌ Tests are slower (real database)
- ❌ Manual setup/teardown
- ❌ Easy to forget cleanup

---

## Problems Summary

### ❌ Without Dependency Injection

| Problem | Impact |
|---------|--------|
| **Manual Object Creation** | Boilerplate code everywhere |
| **Context Dependency** | Android framework pollution |
| **Singleton Pattern** | Global state, threading issues |
| **Service Locator** | Hidden dependencies, runtime errors |
| **ViewModelFactory** | Boilerplate for every ViewModel |
| **Hard to Test** | Need real Android components |
| **Memory Leaks** | Wrong context can leak |
| **No Compile-time Safety** | Runtime crashes |
| **Tight Coupling** | Hard to change implementations |
| **Initialization Order** | Must initialize in correct order |

---

## Benefits of DI

### ✅ With Dependency Injection

| Benefit | Description |
|---------|-------------|
| **Automatic Injection** | Hilt creates and injects dependencies |
| **No Boilerplate** | No ViewModelFactory needed |
| **Easy Testing** | Inject mocks easily |
| **Compile-time Safety** | Errors at compile time |
| **Loose Coupling** | Easy to swap implementations |
| **Clean Architecture** | No framework dependencies in domain |
| **Lifecycle Management** | Hilt manages object lifecycle |
| **Scoping** | @Singleton, @ViewModelScoped, etc. |
| **Thread Safety** | Hilt guarantees thread-safe creation |
| **Qualifiers** | Multiple implementations of same type |

---

## Code Size Comparison

### Repository Creation

**With DI (Current):**
```kotlin
@Singleton
class NotesRepositoryImpl @Inject constructor(
    @LocalDataSourceQualifier private val localDataSource: LocalDataSource,
    @RemoteDataSourceQualifier private val remoteDataSource: RemoteDataSource
) : NotesRepository {
    // Clean implementation
}

// Usage:
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase
) : ViewModel()
```
**Lines of code:** ~10 lines

---

**Without DI (Manual):**
```kotlin
class NotesRepositoryImpl(context: Context) : NotesRepository {
    private val database: NotesDatabase by lazy { /* create */ }
    private val noteDao by lazy { /* create */ }
    private val localDataSource: LocalDataSource by lazy { /* create */ }
    private val remoteDataSource: RemoteDataSource by lazy { /* create */ }
    // Implementation
}

class NotesViewModelFactory(context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Manual dependency tree creation (20+ lines)
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: NotesViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = NotesViewModelFactory(applicationContext)
        // Use factory
    }
}
```
**Lines of code:** ~80+ lines of boilerplate!

---

## Real-World Example

### Scenario: Add New Data Source

**WITH DI:**
1. Create new class with `@Inject` constructor
2. Add `@Binds` in module
3. Done! ✅

```kotlin
class CachedRemoteDataSource @Inject constructor() : RemoteDataSource

@Binds
@CachedQualifier
abstract fun bindCachedRemote(impl: CachedRemoteDataSource): RemoteDataSource
```

---

**WITHOUT DI:**
1. Update ServiceLocator
2. Update ViewModelFactory
3. Update Application class
4. Update all Activities
5. Update test setup
6. Hope you didn't break anything! ❌

```kotlin
// Update ServiceLocator
object ServiceLocator {
    private var cachedRemoteDataSource: RemoteDataSource? = null
    
    fun initialize(context: Context) {
        // Add new initialization
        cachedRemoteDataSource = CachedRemoteDataSource()
    }
    
    fun getCachedRemoteDataSource(): RemoteDataSource {
        return cachedRemoteDataSource ?: throw Exception()
    }
}

// Update ViewModelFactory
class NotesViewModelFactory(context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Add new dependency
        val cachedRemote = ServiceLocator.getCachedRemoteDataSource()
        // ...
    }
}

// Update tests
// Update documentation
// Update everywhere it's used
```

---

## Conclusion

### Why DI (Hilt) is Better

| Aspect | Without DI | With DI |
|--------|-----------|---------|
| **Code Lines** | 80+ lines | 10 lines |
| **Boilerplate** | ViewModelFactory, ServiceLocator | None |
| **Testing** | Hard, needs real Android | Easy, inject mocks |
| **Type Safety** | Runtime errors | Compile-time errors |
| **Maintenance** | High | Low |
| **Learning Curve** | Multiple patterns | One framework |
| **Scalability** | Gets worse | Stays clean |
| **Thread Safety** | Manual | Automatic |

---

## Key Takeaway

**Without DI:** You spend time managing dependencies, creating factories, handling singletons, worrying about memory leaks, and writing boilerplate.

**With DI:** You focus on business logic. Hilt handles all the complexity automatically.

---

## Interview Answer Template

**Q: "Why use Dependency Injection?"**

**A:** "Without DI, we'd have to manually create dependency trees, write ViewModelFactory for every ViewModel, use Service Locator or Singleton patterns which have their own problems like global state and testing difficulties. With Hilt, dependencies are injected automatically, ViewModels are created without factories, testing is easy with mock injection, and we get compile-time safety. It reduces boilerplate from 80+ lines to just 10 lines for the same functionality."

---

**This comparison shows exactly why Dependency Injection is essential for modern Android development!** 🚀
