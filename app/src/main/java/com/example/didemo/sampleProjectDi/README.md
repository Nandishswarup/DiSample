# sampleProjectDi — Hilt/Dagger Teaching Sample

A minimal, self-contained example for learning **Dependency Injection (DI)** with **Hilt** on Android. No repositories, no Room, no network—just the core DI concepts you need to teach or interview with confidence.

---

## What This Sample Demonstrates

| Concept | Where It Appears |
|--------|-------------------|
| **Constructor injection** | `PrintLogger`, `TrackingAnalytics`, `SampleScreenViewModel` |
| **`@Inject constructor()`** | Every injectable class |
| **`@HiltViewModel`** | `SampleScreenViewModel` |
| **`@AndroidEntryPoint`** | `SampleProjectActivity` |
| **`hiltViewModel()`** | `sampleScreenDemo()` Composable |
| **Dependency chain** | ViewModel → Analytics → Logger (no modules needed) |

---

## Dependency Graph

```
SampleScreenViewModel
        │
        │  @Inject constructor(TrackingAnalytics)
        ▼
TrackingAnalytics
        │
        │  @Inject constructor(PrintLogger)
        ▼
PrintLogger
        │
        │  @Inject constructor()  ← no dependencies
        ▼
    (Hilt creates it)
```

**No `@Module` or `@Provides`** are used here. Hilt builds this graph purely from `@Inject` constructors.

---

## File-by-File Walkthrough (Teaching Order)

### 1. `PrintLogger.kt` — Simplest Injectable

```kotlin
class PrintLogger @Inject constructor() {
    fun logMessage(message: String) {
        println("PRINT LOG: $message")
    }
}
```

**Teaching points:**

- **`@Inject constructor()`** tells Hilt: “You may create this class.”
- No parameters ⇒ Hilt just calls this constructor when someone needs a `PrintLogger`.
- This is the **leaf** of the dependency tree (no dependencies of its own).

**Interview question:** *“How does Hilt know how to create `PrintLogger`?”*  
**Answer:** The `@Inject`-annotated constructor. No module required for our own classes.

---

### 2. `TrackingAnalytics.kt` — One Dependency

```kotlin
class TrackingAnalytics @Inject constructor(val printLogger: PrintLogger) {
    fun trackEvent(event: String) {
        printLogger.logMessage(event)
    }
}
```

**Teaching points:**

- **Constructor parameter** `printLogger: PrintLogger` is a dependency.
- Hilt sees `@Inject constructor(PrintLogger)` and will:
  1. Create or reuse a `PrintLogger` (via its `@Inject constructor()`).
  2. Pass it into `TrackingAnalytics`.
- **Dependency inversion:** `TrackingAnalytics` depends on a concrete `PrintLogger` here; in a larger app you’d typically depend on an interface (e.g. `Logger`) and use `@Binds` in a module.

**Interview question:** *“Who creates `PrintLogger` when constructing `TrackingAnalytics`?”*  
**Answer:** Hilt, by resolving the dependency graph and calling `PrintLogger`’s `@Inject constructor()`.

---

### 3. `SampleScreenViewModel.kt` — ViewModel with DI

```kotlin
@HiltViewModel
class SampleScreenViewModel @Inject constructor(val trackingAnalytics: TrackingAnalytics) : ViewModel() {
    val title = mutableStateOf<String>("this is sample screen VM")

    fun trackEvent(event: String) {
        trackingAnalytics.trackEvent(event)
    }
}
```

**Teaching points:**

- **`@HiltViewModel`** means:
  - This is a ViewModel.
  - Hilt will provide a `ViewModelProvider.Factory` that knows how to create it.
  - It’s tied to the correct Android lifecycle (e.g. Activity/Fragment).
- **`@Inject constructor(TrackingAnalytics)`** means:
  - Same rule as above: Hilt must be able to create this class.
  - The only way to do that is via this constructor, so it must be annotated.
- **Both annotations are required.** Without `@Inject constructor`, you get:  
  *“@HiltViewModel annotated class should contain exactly one @Inject or @AssistedInject annotated constructor.”*

**Interview question:** *“Why do we need both `@HiltViewModel` and `@Inject constructor()`?”*  
**Answer:** `@HiltViewModel` tells Hilt it’s a ViewModel and wires it to the ViewModel system; `@Inject constructor()` tells Hilt *how* to create it (which constructor and which dependencies to pass).

---

### 4. `SampleProjectActivity.kt` — Entry Point for DI

```kotlin
@AndroidEntryPoint
class SampleProjectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    sampleScreenDemo()
                }
            }
        }
    }
}
```

**Teaching points:**

- **`@AndroidEntryPoint`** enables Hilt in this Activity.
- Without it, `hiltViewModel()` inside Composables would not work (and you’d get failures when Hilt tries to create the ViewModel).
- The Activity doesn’t inject anything directly; it’s the **host** that allows the Compose UI to use Hilt (e.g. to get `SampleScreenViewModel`).

**Interview question:** *“What happens if you remove `@AndroidEntryPoint`?”*  
**Answer:** Hilt won’t participate in this Activity, so ViewModels and other injections that depend on the Activity’s scope will fail (e.g. `UninitializedPropertyAccessException` or ViewModel creation errors).

---

### 5. `SampleScreen.kt` — Getting the ViewModel in Compose

```kotlin
@Composable
fun sampleScreenDemo() {
    val viewmodel: SampleScreenViewModel = hiltViewModel()

    Column {
        Text(
            text = viewmodel.title.value,
            modifier = Modifier.clickable { viewmodel.trackEvent("click event") }
        )
    }
}
```

**Teaching points:**

- **`hiltViewModel()`** is the Compose API to get a Hilt-managed ViewModel for the current navigation/scope.
- It uses the same `@AndroidEntryPoint` host (the Activity) to obtain the correct Hilt factory and scope.
- No manual `ViewModelProvider` or custom factory; Hilt provides the factory because of `@HiltViewModel` and `@Inject constructor`.

**Interview question:** *“How does `hiltViewModel()` get the right ViewModel?”*  
**Answer:** It uses the Hilt-generated factory for `SampleScreenViewModel`, which was registered because of `@HiltViewModel` and which gets `TrackingAnalytics` (and transitively `PrintLogger`) from the Hilt graph.

---

## End-to-End Flow (When User Taps the Text)

1. User taps the text in `sampleScreenDemo()`.
2. `viewmodel.trackEvent("click event")` is called.
3. `SampleScreenViewModel.trackEvent()` calls `trackingAnalytics.trackEvent("click event")`.
4. `TrackingAnalytics.trackEvent()` calls `printLogger.logMessage("click event")`.
5. `PrintLogger.logMessage()` runs `println("PRINT LOG: click event")`.

All of `PrintLogger`, `TrackingAnalytics`, and `SampleScreenViewModel` were created and wired by **Hilt** via constructor injection; the Activity and Composable only requested the ViewModel.

---

## Prerequisites in the Rest of the App

This sample relies on app-level Hilt setup:

- **Application class** annotated with `@HiltAndroidApp` (e.g. `NotesApplication`).
- That class set as `android:name` in `AndroidManifest.xml`.
- Hilt and Compose dependencies (including `hilt-navigation-compose`) in `build.gradle`.

The `sampleProjectDi` package does **not** define any Hilt modules; the dependency graph is fully expressed with `@Inject` constructors.

---

## How to Use This for Teaching

1. **Start with the graph** — Draw `ViewModel → TrackingAnalytics → PrintLogger` and explain “who creates what.”
2. **Bottom-up** — Explain `PrintLogger`, then `TrackingAnalytics`, then `SampleScreenViewModel`.
3. **Then integration** — Show `@AndroidEntryPoint` and `hiltViewModel()` and run the app; click the text and watch logcat for `PRINT LOG: click event`.
4. **Break things** — Remove `@Inject constructor()` from one class, or `@AndroidEntryPoint` from the Activity, and show the resulting error messages.

---

## Quick Reference: “Why Do We Need…?”

| Thing | Why |
|-------|-----|
| `@Inject constructor()` | Tells Hilt it can and should create this type; required for Hilt to satisfy dependencies. |
| `@HiltViewModel` | Registers the class as a ViewModel and generates the factory used by `viewModels()` / `hiltViewModel()`. |
| `@AndroidEntryPoint` | Enables Hilt in the Activity/Fragment so that Hilt-managed ViewModels and other injections work in that scope. |
| `hiltViewModel()` | In Compose, retrieves the Hilt-created ViewModel for the current scope without a custom factory. |

---

## Summary

**sampleProjectDi** is a minimal Hilt example that shows:

- Constructor injection only (no modules).
- A short dependency chain: ViewModel → Analytics → Logger.
- The three annotations you need for ViewModels: `@HiltViewModel`, `@Inject constructor()`, and (on the host) `@AndroidEntryPoint`.
- How to get that ViewModel in Compose with `hiltViewModel()`.

Use it as a first step before introducing `@Module`, `@Provides`, `@Binds`, scopes, or qualifiers in the main app.
