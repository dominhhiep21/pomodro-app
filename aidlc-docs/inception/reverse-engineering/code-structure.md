# Code Structure

## Build System
- **Type**: Gradle with Kotlin DSL
- **Version Catalog**: `gradle/libs.versions.toml`
- **Kotlin**: 2.3.21
- **Compose Multiplatform**: 1.11.0

## Project Layout
```
Pomodrokotlin/
├── androidApp/          # Android entry
├── desktopApp/          # Desktop JVM entry
├── iosApp/              # iOS SwiftUI entry
├── webApp/              # Web (Wasm/JS) entry - currently disabled
├── shared/              # Shared KMP module
│   └── src/
│       ├── commonMain/kotlin/thong/kotlin/pomodoro/
│       │   ├── App.kt
│       │   ├── Platform.kt
│       │   ├── di/DependencyRegistry.kt
│       │   ├── core/
│       │   │   ├── config/AppConfig.kt
│       │   │   ├── designsystem/{components,theme}/
│       │   │   ├── media/SoundManager.kt
│       │   │   ├── navigation/ScreenNavigation.kt
│       │   │   ├── notification/NotificationManager.kt
│       │   │   ├── pomodoro/mini_client/
│       │   │   └── utils/
│       │   └── features/
│       │       ├── startup/{domain,presentation}/
│       │       ├── onboarding/presentation/
│       │       ├── learning/mode/{components,domain}/
│       │       ├── pomodoro/
│       │       │   ├── _base/{components,data,domain}/
│       │       │   ├── viewmodel/{TimerVM,TasksVM,WorkspaceVM}
│       │       │   ├── timer/{domain,presentation}/
│       │       │   ├── task/{components,domain}/
│       │       │   ├── ambient/{data,domain,presentation}/
│       │       │   └── music/{data,domain,presentation}/
│       │       ├── settings/{data,domain,presentation}/
│       │       └── background/{model,presentation}/
│       ├── androidMain/
│       ├── iosMain/
│       ├── jvmMain/
│       ├── jsMain/
│       └── wasmJsMain/
├── gradle/
└── build.gradle.kts
```

## Key Classes/Modules

### ViewModels
| Class | File | Responsibility |
|-------|------|----------------|
| TimerViewModel | viewmodel/TimerViewModel.kt | Pomodoro countdown, mode switching |
| TasksViewModel | viewmodel/TasksViewModel.kt | Task CRUD operations |
| WorkspaceViewModel | viewmodel/WorkspaceViewModel.kt | UI state (compact, settings, music, ambient) |
| StartupViewModel | startup/presentation/StartupViewModel.kt | App initialization, routing |

### Repositories
| Class | Responsibility |
|-------|----------------|
| UserAppStateRepository | Tasks, sessions, settings, stats (V1) |
| UserAppStateRepositoryV2 | Settings with V2 schema |
| AmbientSoundRepository | Ambient sound list |
| MusicRepository | Music track list |
| BackgroundRepository | Background options |
| StartupRepository | Onboarding state check |

### Screens (Voyager)
| Class | Purpose |
|-------|---------|
| StartupLoadingScreen | Initial loading + route decision |
| OnboardingScreen | First-time setup |
| LearningStyleScreen | Solo/Group selection |
| PomodoroScreenV2 | Main workspace |
| SetupScreen | Settings configuration |

## Design Patterns
- **MVVM**: ViewModel + StateFlow → Compose UI
- **Repository Pattern**: Data abstraction via interfaces
- **Dependency Registry**: Manual DI via singleton object
- **Feature-based packaging**: Each feature has domain/data/presentation
- **Platform expect/actual**: SoundManager, NotificationManager, Platform
