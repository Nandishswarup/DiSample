# 🎯 Dependency Injection - Complete Interview Guide

This document provides a comprehensive guide to all DI concepts demonstrated in this project, perfect for Android interview preparation.

---

## 📋 Table of Contents

1. [What is Dependency Injection?](#what-is-dependency-injection)
2. [Why Use DI?](#why-use-di)
3. [Types of Dependency Injection](#types-of-dependency-injection)
4. [Hilt vs Dagger vs Koin vs Manual DI](#hilt-vs-dagger-vs-koin-vs-manual-di)
5. [Hilt Annotations Deep Dive](#hilt-annotations-deep-dive)
6. [Scopes in Detail](#scopes-in-detail)
7. [Qualifiers Explained](#qualifiers-explained)
8. [Module Types](#module-types)
9. [Common Pitfalls](#common-pitfalls)
10. [Interview Questions & Answers](#interview-questions--answers)

---

## What is Dependency Injection?

### Definition
**Dependency Injection (DI)** is a design pattern where an object receives its dependencies from external sources rather than creating them itself.

### Simple Analogy
Think of a restaurant:
- **Without DI:** Chef grows vegetables, raises chickens, makes utensils (does everything)
- **With DI:** Chef receives ingredients and tools (focuses on cooking)

### Code Example

#### ❌ Without DI (Bad)
```kotlin
class NotesViewModel {
    private val repository = NotesRepositoryImpl(
        LocalDataSourceImpl(
            NoteDao() // How do we even create this?
        ),
        RemoteDataSource()
    )
}
```

**Problems:**
- Hard to test (can't mock repository)
- Tight coupling
- ViewModel knows too much about implementation
- Can't swap implementations

#### ✅ With DI (Good)
```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel()
```

**Benefits:**
- Easy to test (inject mocks)
- Loose coupling
- ViewModel focuses on UI logic
- Easy to swap implementations

---

## Why Use DI?

### 1. **Testability** 🧪
```kotlin
// Easy to test with mocks
class NotesViewModelTest {
    @Test
    fun `test add note`() {
        val mockUseCase = mock<AddNoteUseCase>()
        val viewModel = NotesViewModel(mockUseCase, ...)
        // Test logic
    }
}
```

### 2. **Flexibility** 🔄
```kotlin
// Easy to swap implementations
@Binds
@ProductionQualifier
fun bindProductionDataSource(impl: RetrofitDataSource): RemoteDataSource

@Binds
@DevelopmentQualifier
fun bindDevDataSource(impl: FakeRemoteDataSource): RemoteDataSource
```

### 3. **Separation of Concerns** 🎯
- ViewModel doesn't know about Room
- Repository doesn't know about Retrofit details
- Each layer focuses on its responsibility

### 4. **Lifecycle Management** ♻️
```kotlin
@Singleton // Lives as long as app
@ViewModelScoped // Lives as long as ViewModel
@ActivityScoped // Lives as long as Activity
```

### 5. **Reduced Boilerplate** 📝
No need for:
- ViewModelFactory
- Manual object creation
- Singleton patterns
- Service locators

---

## Types of Dependency Injection

### 1. Constructor Injection (Preferred) ✅
```kotlin
class NotesRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : NotesRepository
```

**Pros:**
- Immutable dependencies
- Easy to test
- Clear dependencies
- Compile-time safety

**Cons:**
- None (always prefer this!)

### 2. Field Injection ⚠️
```kotlin
class MyActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: MyViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hilt injects here
    }
}
```

**Pros:**
- Works when constructor injection isn't possible

**Cons:**
- Mutable (lateinit var)
- Harder to test
- Null safety issues
- Dependencies not clear

**When to use:** Only for Android components where you can't control constructor (Activities, Fragments)

### 3. Method Injection (Rare) 🔷
```kotlin
class MyClass {
    @Inject
    fun initialize(dependency: SomeDependency) {
        // Called by DI framework
    }
}
```

**When to use:** Almost never in Android

---

## Hilt vs Dagger vs Koin vs Manual DI

| Feature | Hilt | Dagger | Koin | Manual DI |
|---------|------|--------|------|-----------|
| **Compile-time safety** | ✅ Yes | ✅ Yes | ❌ No | ✅ Yes |
| **Learning curve** | 🟢 Easy | 🔴 Hard | 🟢 Easy | 🟡 Medium |
| **Boilerplate** | 🟢 Low | 🔴 High | 🟢 Low | 🟡 Medium |
| **Android integration** | ✅ Built-in | ⚠️ Manual | ✅ Built-in | ⚠️ Manual |
| **ViewModel support** | ✅ Excellent | ⚠️ Manual | ✅ Good | ⚠️ Manual |
| **Performance** | ✅ Fast | ✅ Fast | 🟡 Slower | ✅ Fast |
| **Build time** | 🟡 Medium | 🔴 Slow | 🟢 Fast | 🟢 Fast |

### When to use what?

- **Hilt:** Default choice for new Android projects
- **Dagger:** Large projects needing custom components
- **Koin:** Quick prototypes, small projects
- **Manual DI:** Very small apps, learning purposes

---

## Hilt Annotations Deep Dive

### @HiltAndroidApp
```kotlin
@HiltAndroidApp
class NotesApplication : Application()
```

**What it does:**
1. Generates `Hilt_NotesApplication` class
2. Sets up application-level DI container
3. Creates `SingletonComponent`
4. Must be registered in AndroidManifest.xml

**Interview Tip:** This is step 1 of Hilt setup!

---

### @AndroidEntryPoint
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

**What it does:**
1. Generates `Hilt_MainActivity` base class
2. Connects Activity to DI graph
3. Enables field injection
4. Sets up component for this Activity

**Supported on:**
- Activity
- Fragment
- View
- Service
- BroadcastReceiver

**Not supported on:**
- ContentProvider (use EntryPoint instead)

---

### @HiltViewModel
```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val useCase: GetAllNotesUseCase
) : ViewModel()
```

**What it does:**
1. Generates ViewModelFactory
2. Scopes to ViewModelComponent
3. Enables constructor injection
4. Integrates with Compose's `hiltViewModel()`

**Before Hilt:**
```kotlin
// Old way - lots of boilerplate!
class MyViewModelFactory(
    private val useCase: GetAllNotesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(useCase) as T
    }
}
```

**With Hilt:**
```kotlin
// Just this!
@HiltViewModel
class NotesViewModel @Inject constructor(...)
```

---

### @Inject
```kotlin
class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
)
```

**What it does:**
1. Tells Hilt how to create this class
2. All parameters are provided by DI
3. Can be used on constructor, field, or method

**Rules:**
- Only one `@Inject` constructor per class
- All parameters must be providable by Hilt
- Class must not be abstract

---

### @Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(...): NotesDatabase
}
```

**What it does:**
1. Groups related dependencies
2. Provides dependencies that can't use `@Inject`
3. Must be installed in a component

**Types:**
- `object` modules (for @Provides)
- `abstract class` modules (for @Binds)
- `class` modules (rarely used)

---

### @Provides
```kotlin
@Provides
@Singleton
fun provideNotesDatabase(
    @ApplicationContext context: Context
): NotesDatabase {
    return Room.databaseBuilder(
        context,
        NotesDatabase::class.java,
        "notes_db"
    ).build()
}
```

**Use when:**
- You don't own the class (Room, Retrofit, OkHttp)
- Interface returned from builder
- Complex creation logic
- Need to use other dependencies

**Cannot be abstract:** Must have method body

---

### @Binds
```kotlin
@Binds
@Singleton
abstract fun bindNotesRepository(
    impl: NotesRepositoryImpl
): NotesRepository
```

**Use when:**
- Simple interface → implementation binding
- You own the implementation
- Implementation has `@Inject` constructor

**Must be abstract:** No method body

**Why prefer @Binds?**
- More efficient (generates less code)
- Faster compilation
- Cleaner

---

### @InstallIn
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule
```

**Specifies component scope:**
- `SingletonComponent` - Application lifetime
- `ActivityRetainedComponent` - Survives config changes
- `ViewModelComponent` - ViewModel lifetime
- `ActivityComponent` - Activity lifetime
- `FragmentComponent` - Fragment lifetime
- `ViewComponent` - View lifetime
- `ServiceComponent` - Service lifetime

---

## Scopes in Detail

### @Singleton
```kotlin
@Provides
@Singleton
fun provideDatabase(...): NotesDatabase
```

**Lifetime:** Application
**Use for:**
- Database
- Repository
- Network client (Retrofit)
- SharedPreferences

**Interview Question:** "Why make Repository singleton?"
**Answer:** 
- Maintains single source of truth
- Expensive to create
- Shared state across app
- Consistent data

---

### @ViewModelScoped
```kotlin
@ViewModelScoped
class SomeHelper @Inject constructor()
```

**Lifetime:** ViewModel
**Use for:**
- ViewModel-specific helpers
- State managers
- Validators

**Note:** ViewModels themselves don't need this (use @HiltViewModel)

---

### @ActivityScoped
```kotlin
@ActivityScoped
class ActivityHelper @Inject constructor()
```

**Lifetime:** Activity
**Use for:**
- Activity-specific managers
- Navigation helpers
- Permission handlers

---

### Scope Hierarchy
```
@Singleton (Application)
    ↓
@ActivityRetainedScoped (Survives rotation)
    ↓
@ViewModelScoped (ViewModel)
    ↓
@ActivityScoped (Activity)
    ↓
@FragmentScoped (Fragment)
```

**Rule:** Child can depend on parent, not vice versa
- ViewModel can depend on @Singleton ✅
- @Singleton cannot depend on @ViewModelScoped ❌

---

## Qualifiers Explained

### Problem
```kotlin
// How does Hilt know which one to inject?
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
```

### Solution: Qualifiers
```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher
```

### Usage
```kotlin
// Providing
@Provides
@IoDispatcher
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

@Provides
@MainDispatcher
fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

// Injecting
class MyRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
)
```

### Built-in Qualifiers
- `@ApplicationContext` - Application context
- `@ActivityContext` - Activity context

### Custom Qualifiers in This Project
- `@LocalDataSourceQualifier`
- `@RemoteDataSourceQualifier`
- `@IoDispatcher`
- `@MainDispatcher`
- `@DefaultDispatcher`

---

## Module Types

### 1. Object Module (for @Provides)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(...): NotesDatabase {
        return Room.databaseBuilder(...)
    }
}
```

**Use when:** All methods are @Provides

---

### 2. Abstract Class Module (for @Binds)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindLocalDataSource(
        impl: LocalDataSourceImpl
    ): LocalDataSource
}
```

**Use when:** All methods are @Binds

---

### 3. Mixed Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MixedModule {
    @Binds
    abstract fun bindRepository(impl: RepoImpl): Repository
    
    companion object {
        @Provides
        fun provideDatabase(...): Database {
            return Room.databaseBuilder(...)
        }
    }
}
```

**Use when:** Need both @Binds and @Provides

---

## Common Pitfalls

### 1. Circular Dependencies ⚠️
```kotlin
// BAD - Circular dependency!
class A @Inject constructor(b: B)
class B @Inject constructor(a: A)
```

**Solution:** Refactor to remove circular dependency

---

### 2. Missing @InstallIn
```kotlin
// BAD - Compilation error!
@Module
object MyModule {
    @Provides
    fun provideSomething(): Something
}
```

**Solution:** Always add @InstallIn
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MyModule
```

---

### 3. Wrong Scope
```kotlin
// BAD - Singleton depending on ViewModelScoped
@Singleton
class Repository @Inject constructor(
    @ViewModelScoped private val helper: Helper // Error!
)
```

**Solution:** Parent scope can't depend on child scope

---

### 4. Forgetting @AndroidEntryPoint
```kotlin
// BAD - Field injection won't work!
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: MyViewModel // Null!
}
```

**Solution:** Add @AndroidEntryPoint
```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity()
```

---

### 5. Multiple @Inject Constructors
```kotlin
// BAD - Compilation error!
class MyClass @Inject constructor(a: A) {
    @Inject constructor(b: B) // Error!
}
```

**Solution:** Only one @Inject constructor allowed

---

## Interview Questions & Answers

### Q1: What is Dependency Injection?
**A:** A design pattern where objects receive their dependencies from external sources rather than creating them. Benefits include testability, flexibility, and loose coupling.

### Q2: Why use Hilt over manual DI?
**A:** 
- Reduces boilerplate (no ViewModelFactory)
- Compile-time safety
- Android lifecycle awareness
- Standardized approach
- Built-in scoping

### Q3: Explain @Binds vs @Provides
**A:**
- **@Binds:** For interface → implementation binding. More efficient, generates less code. Must be abstract.
- **@Provides:** For complex object creation, third-party libraries. Has method body.

### Q4: What does @HiltAndroidApp do?
**A:** 
- Marks Application class as entry point
- Generates application-level DI container
- Creates SingletonComponent
- Must be first step in Hilt setup

### Q5: How do you inject different implementations of the same interface?
**A:** Use custom @Qualifier annotations to distinguish between them.
```kotlin
@LocalDataSourceQualifier
@RemoteDataSourceQualifier
```

### Q6: What are scopes in Hilt?
**A:** Scopes control the lifetime of dependencies:
- @Singleton - Application lifetime
- @ViewModelScoped - ViewModel lifetime
- @ActivityScoped - Activity lifetime
Rule: Child can depend on parent, not vice versa

### Q7: Why is constructor injection preferred?
**A:**
- Immutable dependencies
- Easy to test
- Clear dependencies
- Compile-time safety
- No reflection needed

### Q8: What's the component hierarchy in Hilt?
**A:**
```
SingletonComponent (Application)
  ↓
ActivityRetainedComponent
  ↓
ViewModelComponent
  ↓
ActivityComponent
  ↓
FragmentComponent
```

### Q9: How does Hilt integrate with Jetpack Compose?
**A:** Use `hiltViewModel()` function in Composables. It automatically gets ViewModel from Hilt without manual factory creation.

### Q10: What's the difference between Hilt and Dagger?
**A:**
- Hilt is built on Dagger
- Hilt reduces boilerplate
- Hilt has predefined Android components
- Hilt has better ViewModel support
- Hilt is easier to learn

### Q11: How do you test with Hilt?
**A:**
- Use @HiltAndroidTest
- Replace modules with test implementations
- Use @UninstallModules to remove production modules
- Inject mocks easily

### Q12: What is a circular dependency and how to avoid it?
**A:** When A depends on B and B depends on A. Avoid by:
- Refactoring code structure
- Using interfaces
- Breaking into smaller components
- Using events/callbacks

### Q13: Why make Repository singleton?
**A:**
- Single source of truth
- Maintains consistency
- Expensive to create
- Shared state across app
- Cache management

### Q14: What's the purpose of @InstallIn?
**A:** Specifies which component the module should be installed in, determining the lifetime and scope of provided dependencies.

### Q15: Can you use Hilt in a multi-module project?
**A:** Yes! Each module can have its own Hilt modules. The app module aggregates all dependencies.

---

## 🎯 Key Takeaways for Interviews

1. **Always prefer constructor injection** over field injection
2. **Use @Binds when possible**, @Provides when necessary
3. **Understand scope hierarchy** - child can depend on parent
4. **Qualifiers solve ambiguity** - multiple implementations of same type
5. **Hilt reduces boilerplate** - no ViewModelFactory needed
6. **Testability is key benefit** - easy to inject mocks
7. **Know the component hierarchy** - SingletonComponent → ViewModelComponent → ActivityComponent
8. **@HiltAndroidApp is step 1** - must be on Application class

---

## 📚 Further Reading

- [Official Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Dependency Injection Principles](https://martinfowler.com/articles/injection.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Remember:** The goal of DI is to make code more testable, flexible, and maintainable. Hilt makes this easy in Android! 🚀
