# 📱 Offline-First Notes App - Dependency Injection Demo

A production-ready Android application demonstrating **Clean Architecture**, **Hilt Dependency Injection**, and **Offline-First** patterns using Jetpack Compose.

Perfect for Android interview preparation! 🎯

## 🎓 Learning Objectives

This project demonstrates:
- ✅ **Hilt DI** with all key concepts
- ✅ **Clean Architecture** with proper layer separation
- ✅ **Offline-First** architecture pattern
- ✅ **Jetpack Compose** modern UI
- ✅ **Room Database** for local persistence
- ✅ **Coroutines & Flow** for async operations
- ✅ **MVVM** with ViewModel

---

## 🏗️ Architecture Overview

```
app/
├── presentation/          # UI Layer (Compose + ViewModel)
│   └── notes/
│       ├── NotesScreen.kt
│       └── NotesViewModel.kt
│
├── domain/               # Business Logic Layer
│   ├── model/
│   │   └── Note.kt
│   ├── repository/
│   │   └── NotesRepository.kt (interface)
│   └── usecase/
│       ├── GetAllNotesUseCase.kt
│       ├── AddNoteUseCase.kt
│       ├── UpdateNoteUseCase.kt
│       ├── DeleteNoteUseCase.kt
│       └── SyncNotesUseCase.kt
│
├── data/                 # Data Layer
│   ├── local/
│   │   ├── entity/
│   │   │   └── NoteEntity.kt
│   │   ├── dao/
│   │   │   └── NoteDao.kt
│   │   ├── database/
│   │   │   └── NotesDatabase.kt
│   │   └── LocalDataSource.kt
│   ├── remote/
│   │   └── RemoteDataSource.kt
│   └── repository/
│       └── NotesRepositoryImpl.kt
│
└── di/                   # Dependency Injection
    ├── qualifier/
    │   └── DataSourceQualifiers.kt
    └── module/
        ├── DatabaseModule.kt
        ├── DataSourceModule.kt
        ├── RepositoryModule.kt
        └── DispatcherModule.kt
```

---

## 🔌 Dependency Injection Concepts

### 1️⃣ @HiltAndroidApp
**Location:** `NotesApplication.kt`

```kotlin
@HiltAndroidApp
class NotesApplication : Application()
```

**Purpose:**
- Entry point for Hilt DI
- Generates application-level dependency container
- Must be registered in AndroidManifest.xml

**Interview Tip:** This is the FIRST step to set up Hilt!

---

### 2️⃣ @AndroidEntryPoint
**Location:** `MainActivity.kt`

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

**Purpose:**
- Enables DI in Android components
- Required for Activities, Fragments, Views, Services
- Connects component to Hilt dependency graph

**Interview Question:** "What does @AndroidEntryPoint do?"
- Marks Android components for injection
- Generates base class with injection setup
- Must be on parent if children need injection

---

### 3️⃣ @HiltViewModel
**Location:** `NotesViewModel.kt`

```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel()
```

**Purpose:**
- Enables Hilt injection in ViewModel
- Automatically creates ViewModelFactory
- Scoped to ViewModel lifecycle

**Benefits:**
- No ViewModelFactory boilerplate
- Easy to test with mock dependencies
- Survives configuration changes

---

### 4️⃣ @Inject Constructor
**Location:** All use cases, data sources, repository

```kotlin
class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
)
```

**Purpose:**
- Tells Hilt how to create instances
- Constructor injection (preferred method)
- All parameters provided automatically

**Interview Tip:** Use constructor injection whenever possible!

---

### 5️⃣ @Module & @InstallIn
**Location:** All DI modules

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
```

**Purpose:**
- @Module: Tells Hilt this provides dependencies
- @InstallIn: Specifies component scope

**Component Scopes:**
- `SingletonComponent` → Application lifetime
- `ViewModelComponent` → ViewModel lifetime
- `ActivityComponent` → Activity lifetime
- `FragmentComponent` → Fragment lifetime

---

### 6️⃣ @Provides vs @Binds

#### @Provides (DatabaseModule.kt)
```kotlin
@Provides
@Singleton
fun provideNotesDatabase(
    @ApplicationContext context: Context
): NotesDatabase {
    return Room.databaseBuilder(...)
}
```

**Use when:**
- You don't own the class (Room, Retrofit)
- Constructor injection isn't possible
- Complex creation logic needed

#### @Binds (DataSourceModule.kt)
```kotlin
@Binds
@Singleton
abstract fun bindLocalDataSource(
    impl: LocalDataSourceImpl
): LocalDataSource
```

**Use when:**
- Simple interface → implementation binding
- More efficient than @Provides
- Generates less code

**Interview Question:** "When to use @Binds vs @Provides?"
- **@Binds:** Interface to implementation (simpler, efficient)
- **@Provides:** Complex object creation, builders, third-party libs

---

### 7️⃣ @Singleton Scope
**Location:** Repository, Database, DAOs

```kotlin
@Singleton
class NotesRepositoryImpl @Inject constructor(...)
```

**Purpose:**
- Single instance across the app
- Lives as long as the application
- Shared by all ViewModels

**When to use:**
- Database (expensive to create)
- Repository (maintain consistency)
- Network clients (Retrofit)

---

### 8️⃣ Custom Qualifiers
**Location:** `DataSourceQualifiers.kt`

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSourceQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSourceQualifier
```

**Usage in Repository:**
```kotlin
@Singleton
class NotesRepositoryImpl @Inject constructor(
    @LocalDataSourceQualifier private val localDataSource: LocalDataSource,
    @RemoteDataSourceQualifier private val remoteDataSource: RemoteDataSource
)
```

**Purpose:**
- Distinguish between multiple implementations
- Same interface, different instances

**Interview Question:** "How do you provide multiple implementations of the same interface?"
**Answer:** Use custom @Qualifier annotations!

**Real-world examples:**
- `@LocalDataSource` vs `@RemoteDataSource`
- `@ProductionApi` vs `@MockApi`
- `@IoDispatcher` vs `@MainDispatcher`

---

### 9️⃣ Dispatcher Injection
**Location:** `DispatcherModule.kt`

```kotlin
@Provides
@IoDispatcher
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

@Provides
@MainDispatcher
fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
```

**Purpose:**
- Provide different dispatchers for different purposes
- Makes code testable (inject TestDispatcher in tests)

---

## 🔄 Dependency Graph

```
NotesViewModel
    ↓
GetAllNotesUseCase, AddNoteUseCase, etc.
    ↓
NotesRepository (interface)
    ↓
NotesRepositoryImpl
    ↓
├─ LocalDataSource (interface)
│     ↓
│  LocalDataSourceImpl
│     ↓
│  NoteDao
│     ↓
│  NotesDatabase
│
└─ RemoteDataSource (interface)
      ↓
   FakeRemoteDataSource
```

**Hilt automatically creates and manages this entire graph!**

---

## 🎯 Key Interview Questions & Answers

### Q1: What is Dependency Injection?
**A:** A design pattern where dependencies are provided to a class rather than the class creating them itself. Benefits: testability, flexibility, loose coupling.

### Q2: Why use Hilt over manual DI?
**A:** 
- Less boilerplate code
- Compile-time safety
- Android lifecycle awareness
- Built-in ViewModel support
- Standardized approach

### Q3: What's the difference between @Binds and @Provides?
**A:**
- **@Binds:** For simple interface → implementation binding (more efficient)
- **@Provides:** For complex object creation, builders, third-party libraries

### Q4: How do you inject dependencies into a ViewModel?
**A:** Use `@HiltViewModel` annotation and `@Inject` constructor. Hilt automatically creates the ViewModelFactory.

### Q5: What are Qualifiers and when do you use them?
**A:** Qualifiers distinguish between multiple implementations of the same type. Use custom `@Qualifier` annotations like `@LocalDataSource` and `@RemoteDataSource`.

### Q6: Explain the component hierarchy in Hilt.
**A:**
```
SingletonComponent (Application)
    ↓
ActivityRetainedComponent
    ↓
ViewModelComponent (ViewModel)
    ↓
ActivityComponent (Activity)
    ↓
FragmentComponent (Fragment)
    ↓
ViewComponent (View)
```

### Q7: What is the Repository pattern and how does DI help?
**A:** Repository abstracts data sources. Domain depends on interface, data layer implements it. DI injects the correct implementation, making it easy to swap data sources and test.

### Q8: Why is constructor injection preferred?
**A:**
- Immutable dependencies
- Easy to test
- Clear dependencies
- Compile-time safety
- No reflection needed

---

## 🚀 Features

- ✅ Add, edit, delete notes
- ✅ Offline-first (local database as source of truth)
- ✅ Sync status indicator
- ✅ Material 3 Design
- ✅ Dark mode support
- ✅ Reactive UI with Flow
- ✅ Clean Architecture
- ✅ Comprehensive DI setup

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| DI | Hilt |
| Database | Room |
| Async | Coroutines + Flow |
| Architecture | Clean Architecture + MVVM |
| Build | Gradle (KTS) |

---

## 📦 Dependencies

```kotlin
// Hilt
implementation("com.google.dagger:hilt-android:2.52")
ksp("com.google.dagger:hilt-compiler:2.52")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.12.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.9.3")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
```

---

## 🏃 How to Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device

---

## 🧪 Testing Strategy

### Unit Tests
- Mock use cases in ViewModel tests
- Mock repository in use case tests
- Test business logic in isolation

### Integration Tests
- Use Hilt's `@HiltAndroidTest`
- Replace modules with test implementations
- Test full dependency graph

---

## 📚 Additional Resources

- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)

---

## 🎓 Interview Preparation Tips

1. **Understand the dependency graph** - Be able to draw it on a whiteboard
2. **Know when to use @Binds vs @Provides** - Common interview question
3. **Explain scopes** - Singleton, ViewModelScoped, ActivityScoped
4. **Qualifiers** - How to provide multiple implementations
5. **Testing benefits** - How DI makes testing easier
6. **Compare with other DI frameworks** - Hilt vs Dagger vs Koin

---

## 📝 License

This project is for educational purposes - Android interview preparation.

---

## 👨‍💻 Author

Created as a comprehensive DI demonstration for Android interviews.

**Key Takeaway:** This project demonstrates production-ready code with proper architecture, dependency injection, and best practices. Perfect for showcasing in interviews! 🚀
