# 🏗️ Architecture Documentation

## Clean Architecture Layers

This project follows **Clean Architecture** principles with strict separation of concerns.

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌──────────────┐              ┌──────────────┐            │
│  │ NotesScreen  │──────────────│NotesViewModel│            │
│  │  (Compose)   │              │ (@HiltViewModel)│          │
│  └──────────────┘              └───────┬──────┘            │
│                                        │                     │
└────────────────────────────────────────┼─────────────────────┘
                                         │ depends on
┌────────────────────────────────────────┼─────────────────────┐
│                      DOMAIN LAYER      │                     │
│                                        ▼                     │
│  ┌──────────────────────────────────────────────────┐       │
│  │              Use Cases (Business Logic)          │       │
│  ├──────────────────────────────────────────────────┤       │
│  │  • GetAllNotesUseCase                           │       │
│  │  • AddNoteUseCase                               │       │
│  │  • UpdateNoteUseCase                            │       │
│  │  • DeleteNoteUseCase                            │       │
│  │  • SyncNotesUseCase                             │       │
│  └─────────────────────┬────────────────────────────┘       │
│                        │ depends on                         │
│  ┌─────────────────────▼────────────────────────────┐       │
│  │         NotesRepository (Interface)              │       │
│  └──────────────────────────────────────────────────┘       │
│                                                              │
│  ┌──────────────────────────────────────────────────┐       │
│  │              Note (Domain Model)                 │       │
│  └──────────────────────────────────────────────────┘       │
└────────────────────────────────────────┼─────────────────────┘
                                         │ implements
┌────────────────────────────────────────┼─────────────────────┐
│                       DATA LAYER       │                     │
│                                        ▼                     │
│  ┌──────────────────────────────────────────────────┐       │
│  │        NotesRepositoryImpl (@Singleton)          │       │
│  └────────────┬──────────────────────┬──────────────┘       │
│               │                      │                       │
│               ▼                      ▼                       │
│  ┌────────────────────┐  ┌────────────────────┐            │
│  │  LocalDataSource   │  │ RemoteDataSource   │            │
│  │ (@LocalQualifier)  │  │(@RemoteQualifier)  │            │
│  └─────────┬──────────┘  └────────────────────┘            │
│            │                                                 │
│            ▼                                                 │
│  ┌────────────────────┐                                     │
│  │     NoteDao        │                                     │
│  └─────────┬──────────┘                                     │
│            │                                                 │
│            ▼                                                 │
│  ┌────────────────────┐                                     │
│  │   NotesDatabase    │                                     │
│  │   (Room @Singleton)│                                     │
│  └────────────────────┘                                     │
└─────────────────────────────────────────────────────────────┘
```

---

## Dependency Injection Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    @HiltAndroidApp                          │
│                  NotesApplication                           │
│                        │                                    │
│                        ▼                                    │
│              ┌──────────────────┐                           │
│              │SingletonComponent│                           │
│              └────────┬─────────┘                           │
│                       │                                     │
│         ┌─────────────┼─────────────┐                       │
│         ▼             ▼             ▼                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │Database  │  │DataSource│  │Repository│                 │
│  │Module    │  │Module    │  │Module    │                 │
│  └──────────┘  └──────────┘  └──────────┘                 │
└─────────────────────────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              @AndroidEntryPoint                             │
│                MainActivity                                 │
│                     │                                       │
│                     ▼                                       │
│            ┌─────────────────┐                              │
│            │ ActivityComponent│                             │
│            └────────┬─────────┘                             │
│                     │                                       │
│                     ▼                                       │
│            ┌─────────────────┐                              │
│            │ViewModelComponent│                             │
│            └────────┬─────────┘                             │
│                     │                                       │
│                     ▼                                       │
│            ┌─────────────────┐                              │
│            │  @HiltViewModel │                              │
│            │  NotesViewModel │                              │
│            └─────────────────┘                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Data Flow - Offline First

```
┌──────────────┐
│    User      │
│  Interaction │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│  NotesScreen     │  Compose UI
│  (Presentation)  │
└──────┬───────────┘
       │ User Action (Add/Delete/Update)
       ▼
┌──────────────────┐
│ NotesViewModel   │  State Management
│                  │  • StateFlow
│                  │  • SharedFlow
└──────┬───────────┘
       │ Call Use Case
       ▼
┌──────────────────┐
│   Use Cases      │  Business Logic
│                  │  • Validation
│                  │  • Error Handling
└──────┬───────────┘
       │ Repository Interface
       ▼
┌──────────────────┐
│NotesRepositoryImpl│ Data Coordination
└──────┬───────────┘
       │
       ├─────────────────────────┐
       │                         │
       ▼                         ▼
┌──────────────┐         ┌──────────────┐
│LocalDataSource│         │RemoteDataSource│
│  (Primary)   │         │  (Secondary)  │
└──────┬───────┘         └──────┬────────┘
       │                        │
       ▼                        ▼
┌──────────────┐         ┌──────────────┐
│ Room Database│         │  API/Server  │
│ (Source of   │         │  (Sync only) │
│   Truth)     │         │              │
└──────────────┘         └──────────────┘

Flow Direction:
1. User adds note → ViewModel → Use Case → Repository
2. Repository saves to Local (Room) FIRST
3. Room emits Flow update
4. UI updates immediately (offline-first!)
5. Background sync to Remote (when available)
```

---

## Dependency Graph

```
NotesViewModel
    │
    ├── GetAllNotesUseCase ──┐
    ├── AddNoteUseCase ──────┤
    ├── UpdateNoteUseCase ───┤
    ├── DeleteNoteUseCase ───┤
    └── SyncNotesUseCase ────┤
                             │
                             ▼
                    NotesRepository (interface)
                             │
                             ▼
                    NotesRepositoryImpl (@Singleton)
                             │
                ┌────────────┴────────────┐
                │                         │
                ▼                         ▼
    LocalDataSource              RemoteDataSource
    (@LocalQualifier)            (@RemoteQualifier)
                │                         │
                ▼                         ▼
    LocalDataSourceImpl          FakeRemoteDataSource
                │
                ▼
            NoteDao
                │
                ▼
         NotesDatabase (@Singleton)
                │
                ▼
         Room.databaseBuilder()
                │
                ▼
         ApplicationContext
```

**Key Points:**
- All dependencies flow downward
- No upward dependencies (Dependency Inversion)
- Interfaces at boundaries
- Hilt manages entire graph automatically

---

## Module Organization

```
di/
├── qualifier/
│   └── DataSourceQualifiers.kt
│       ├── @LocalDataSourceQualifier
│       ├── @RemoteDataSourceQualifier
│       ├── @IoDispatcher
│       ├── @MainDispatcher
│       └── @DefaultDispatcher
│
└── module/
    ├── DatabaseModule.kt
    │   ├── @Provides NotesDatabase
    │   └── @Provides NoteDao
    │
    ├── DataSourceModule.kt
    │   ├── @Binds LocalDataSource
    │   └── @Binds RemoteDataSource
    │
    ├── RepositoryModule.kt
    │   └── @Binds NotesRepository
    │
    └── DispatcherModule.kt
        ├── @Provides @IoDispatcher
        ├── @Provides @MainDispatcher
        └── @Provides @DefaultDispatcher
```

---

## Scope Hierarchy

```
@Singleton (Application Lifetime)
    │
    ├── NotesDatabase
    ├── NotesRepositoryImpl
    ├── LocalDataSourceImpl
    ├── FakeRemoteDataSource
    └── Dispatchers
    
    ↓
    
@ActivityRetainedScoped (Survives Configuration Changes)
    │
    └── (Not used in this project)
    
    ↓
    
@ViewModelScoped (ViewModel Lifetime)
    │
    └── NotesViewModel (uses @HiltViewModel)
        │
        ├── GetAllNotesUseCase (no scope - new instance)
        ├── AddNoteUseCase (no scope - new instance)
        └── ... (other use cases)
    
    ↓
    
@ActivityScoped (Activity Lifetime)
    │
    └── (Not used in this project)
```

**Scoping Strategy:**
- **@Singleton:** Expensive objects (Database, Repository)
- **No Scope:** Lightweight objects (Use Cases)
- **@HiltViewModel:** Automatic for ViewModels

---

## Testing Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                      UNIT TESTS                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  NotesViewModelTest                                         │
│    ├── Mock: GetAllNotesUseCase                            │
│    ├── Mock: AddNoteUseCase                                │
│    └── Test: ViewModel logic                               │
│                                                              │
│  AddNoteUseCaseTest                                         │
│    ├── Mock: NotesRepository                               │
│    └── Test: Validation logic                              │
│                                                              │
│  NotesRepositoryImplTest                                    │
│    ├── Mock: LocalDataSource                               │
│    ├── Mock: RemoteDataSource                              │
│    └── Test: Repository logic                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   INTEGRATION TESTS                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  @HiltAndroidTest                                           │
│  NotesRepositoryIntegrationTest                             │
│    ├── Real: Room Database (in-memory)                     │
│    ├── Mock: RemoteDataSource                              │
│    └── Test: Full repository flow                          │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      UI TESTS                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  @HiltAndroidTest                                           │
│  NotesScreenTest                                            │
│    ├── Real: ViewModel                                      │
│    ├── Mock: Use Cases                                      │
│    └── Test: Compose UI interactions                       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Design Patterns

### 1. Repository Pattern
- Abstracts data sources
- Single source of truth
- Easy to swap implementations

### 2. Use Case Pattern
- Single responsibility
- Encapsulates business logic
- Reusable across ViewModels

### 3. Observer Pattern
- Flow for reactive updates
- UI observes state changes
- Automatic UI updates

### 4. Dependency Injection
- Inversion of Control
- Loose coupling
- Easy testing

### 5. Clean Architecture
- Layer separation
- Dependency rule (inward only)
- Framework independence

---

## Benefits of This Architecture

### ✅ Testability
- Each layer can be tested independently
- Easy to mock dependencies
- Fast unit tests

### ✅ Maintainability
- Clear separation of concerns
- Easy to understand
- Easy to modify

### ✅ Scalability
- Easy to add features
- Easy to add data sources
- Modular structure

### ✅ Flexibility
- Easy to swap implementations
- Multiple data sources
- Platform independence

### ✅ Offline-First
- Works without network
- Local database as source of truth
- Background sync

---

## Interview Talking Points

1. **Clean Architecture Benefits**
   - Separation of concerns
   - Testability
   - Independence from frameworks

2. **Offline-First Strategy**
   - Local database is source of truth
   - Immediate UI updates
   - Background sync

3. **DI Benefits**
   - Loose coupling
   - Easy testing
   - Lifecycle management

4. **Use Case Pattern**
   - Single responsibility
   - Reusable business logic
   - Easy to test

5. **Repository Pattern**
   - Abstracts data sources
   - Single source of truth
   - Easy to swap implementations

---

## Future Enhancements

- [ ] Add WorkManager for background sync
- [ ] Implement pagination for large datasets
- [ ] Add search functionality
- [ ] Implement note categories/tags
- [ ] Add image attachments
- [ ] Implement note sharing
- [ ] Add encryption for sensitive notes
- [ ] Multi-user support with authentication

---

**This architecture is production-ready and demonstrates best practices for Android development!** 🚀
