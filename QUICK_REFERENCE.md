# 🚀 Quick Reference Guide

## 📱 App Overview
**Offline-First Notes App** with Clean Architecture + Hilt DI

---

## 🎯 DI Patterns Cheat Sheet

### Setup (Required)
```kotlin
// 1. Application class
@HiltAndroidApp
class NotesApplication : Application()

// 2. Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// 3. ViewModel
@HiltViewModel
class NotesViewModel @Inject constructor(...)
```

### Constructor Injection (Preferred)
```kotlin
class MyClass @Inject constructor(
    private val dependency: SomeDependency
)
```

### Module with @Provides
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MyModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(...)
    }
}
```

### Module with @Binds
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MyModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: RepoImpl): Repository
}
```

### Custom Qualifier
```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

// Usage
@Provides
@LocalDataSource
fun provideLocal(): DataSource = LocalDataSourceImpl()

// Injection
class Repo @Inject constructor(
    @LocalDataSource private val local: DataSource
)
```

---

## 📂 File Locations

### DI Setup
- `NotesApplication.kt` - @HiltAndroidApp
- `MainActivity.kt` - @AndroidEntryPoint
- `NotesViewModel.kt` - @HiltViewModel

### DI Modules
- `di/module/DatabaseModule.kt` - Database + DAO
- `di/module/DataSourceModule.kt` - Data sources
- `di/module/RepositoryModule.kt` - Repository
- `di/module/DispatcherModule.kt` - Coroutine dispatchers

### Qualifiers
- `di/qualifier/DataSourceQualifiers.kt` - All custom qualifiers

### Domain Layer
- `domain/model/Note.kt` - Domain model
- `domain/repository/NotesRepository.kt` - Interface
- `domain/usecase/` - All use cases

### Data Layer
- `data/local/entity/NoteEntity.kt` - Room entity
- `data/local/dao/NoteDao.kt` - Room DAO
- `data/local/database/NotesDatabase.kt` - Room DB
- `data/local/LocalDataSource.kt` - Local data source
- `data/remote/RemoteDataSource.kt` - Remote data source
- `data/repository/NotesRepositoryImpl.kt` - Repository impl

### Presentation Layer
- `presentation/notes/NotesScreen.kt` - Compose UI
- `presentation/notes/NotesViewModel.kt` - ViewModel

---

## 🔑 Key Annotations

| Annotation | Purpose | Location |
|------------|---------|----------|
| `@HiltAndroidApp` | App entry point | Application class |
| `@AndroidEntryPoint` | Enable DI | Activity/Fragment |
| `@HiltViewModel` | ViewModel DI | ViewModel |
| `@Inject` | Constructor injection | Everywhere |
| `@Module` | Provide dependencies | DI modules |
| `@InstallIn` | Component scope | DI modules |
| `@Provides` | Complex creation | Module methods |
| `@Binds` | Interface binding | Abstract methods |
| `@Singleton` | App-wide scope | Classes/methods |
| `@Qualifier` | Distinguish types | Custom annotations |

---

## 🎓 Interview Questions Quick Answers

**Q: What is DI?**
A: Pattern where objects receive dependencies instead of creating them. Benefits: testability, flexibility, loose coupling.

**Q: Why Hilt?**
A: Less boilerplate, compile-time safety, Android integration, ViewModel support.

**Q: @Binds vs @Provides?**
A: @Binds for simple interface→impl (efficient). @Provides for complex creation.

**Q: How to inject ViewModel?**
A: Use @HiltViewModel + @Inject constructor. Access with hiltViewModel() in Compose.

**Q: What are Qualifiers?**
A: Distinguish multiple implementations of same type. Use custom @Qualifier annotations.

**Q: What are scopes?**
A: Control lifetime. @Singleton (app), @ViewModelScoped (VM), @ActivityScoped (activity).

**Q: Component hierarchy?**
A: SingletonComponent → ViewModelComponent → ActivityComponent → FragmentComponent

**Q: Why constructor injection?**
A: Immutable, testable, clear dependencies, compile-time safe.

---

## 📊 Dependency Graph

```
ViewModel
  ↓
Use Cases
  ↓
Repository (interface)
  ↓
RepositoryImpl
  ↓
├─ LocalDataSource → DAO → Database
└─ RemoteDataSource → API
```

---

## 🏗️ Architecture Layers

```
Presentation (UI + ViewModel)
    ↓ depends on
Domain (Business Logic + Interfaces)
    ↓ implements
Data (Repository + Data Sources)
```

---

## 🔄 Data Flow

```
User Action
  ↓
Compose UI
  ↓
ViewModel
  ↓
Use Case
  ↓
Repository
  ↓
Local DB (Source of Truth)
  ↓
Flow emits
  ↓
UI updates
```

---

## 📝 Code Snippets

### ViewModel with DI
```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {
    val notes = getAllNotesUseCase().stateIn(...)
}
```

### Use Case
```kotlin
class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}
```

### Repository
```kotlin
@Singleton
class NotesRepositoryImpl @Inject constructor(
    @LocalDataSourceQualifier private val local: LocalDataSource,
    @RemoteDataSourceQualifier private val remote: RemoteDataSource
) : NotesRepository {
    override fun getAllNotes() = local.getAllNotes().map { ... }
}
```

### Compose with Hilt
```kotlin
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    // UI code
}
```

---

## 🧪 Testing

### ViewModel Test
```kotlin
class NotesViewModelTest {
    @Test
    fun `test add note`() {
        val mockUseCase = mock<AddNoteUseCase>()
        val viewModel = NotesViewModel(mockUseCase, ...)
        // Test
    }
}
```

### Hilt Test
```kotlin
@HiltAndroidTest
class RepositoryTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: NotesRepository
    
    @Test
    fun testRepository() {
        hiltRule.inject()
        // Test with real DI
    }
}
```

---

## 🎯 Best Practices

✅ **DO:**
- Use constructor injection
- Prefer @Binds over @Provides
- Use @Singleton for expensive objects
- Create interfaces for abstractions
- Use qualifiers for multiple implementations
- Document your DI setup

❌ **DON'T:**
- Use field injection unless necessary
- Create circular dependencies
- Forget @InstallIn on modules
- Mix scopes incorrectly
- Overuse @Singleton

---

## 🚀 Run the App

```bash
# Open in Android Studio
# Sync Gradle
# Run on emulator/device
```

---

## 📚 Documentation Files

- `README.md` - Main documentation
- `DI_INTERVIEW_GUIDE.md` - Complete DI guide
- `ARCHITECTURE.md` - Architecture diagrams
- `PROJECT_SUMMARY.md` - Project overview
- `QUICK_REFERENCE.md` - This file

---

## 💡 Key Takeaways

1. **Hilt simplifies DI** - No boilerplate
2. **@Binds is efficient** - Use when possible
3. **Qualifiers solve ambiguity** - Multiple implementations
4. **Clean Architecture scales** - Easy to extend
5. **Offline-first works** - Local DB as source of truth

---

**Perfect for Android interviews! 🎯**
