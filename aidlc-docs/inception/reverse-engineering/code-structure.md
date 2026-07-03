# Code Structure

## Build System
- **Type**: Gradle with Kotlin DSL
- **Version Catalog**: `gradle/libs.versions.toml`
- **Kotlin**: 2.3.21
- **Compose Multiplatform**: 1.11.0
- **SQLDelight**: 2.3.2

## Project Layout
```
Pomodrokotlin/
├── androidApp/          # Android entry
├── desktopApp/          # Desktop JVM entry
├── iosApp/              # iOS SwiftUI entry (planned)
├── webApp/              # Web entry (disabled)
├── shared/              # Shared KMP module
│   └── src/
│       ├── commonMain/kotlin/thong/kotlin/pomodoro/
│       │   ├── App.kt
│       │   ├── Platform.kt
│       │   ├── ExampleApp.kt
│       │   ├── di/DependencyRegistry.kt
│       │   ├── database/DatabaseDriverFactory.kt
│       │   ├── core/
│       │   │   ├── config/AppConfig.kt
│       │   │   ├── designsystem/
│       │   │   │   ├── components/ (GlassBox, AuraButton, AuraInputField, AuraDialog, etc.)
│       │   │   │   └── theme/ (AuraColors, AuraGradients, AuraAnimations, AuraTheme, AuraTypography)
│       │   │   ├── media/SoundManager.kt
│       │   │   ├── navigation/ScreenNavigation.kt
│       │   │   ├── notification/NotificationManager.kt
│       │   │   ├── pomodoro/mini_client/ (PomodoroMiniClient, Models)
│       │   │   └── utils/ (DateUtils, ScrollUtils, TimeFormatter, InlineFunctionUtil)
│       │   └── features/
│       │       ├── main/MainTabScreen.kt
│       │       ├── startup/presentation/StartupScreen.kt
│       │       ├── onboarding/presentation/OnboardingView.kt
│       │       ├── learning/mode/ (components, domain)
│       │       ├── pomodoro/
│       │       │   ├── _base/ (PomodoroScreenV2, components, data, domain)
│       │       │   ├── viewmodel/ (AppViewModel, TimerUiState, WorkspaceUiState, TasksUiState)
│       │       │   ├── timer/ (domain, presentation)
│       │       │   ├── task/ (components, domain)
│       │       │   ├── ambient/ (data, domain, presentation)
│       │       │   └── music/ (data, domain, presentation)
│       │       ├── session/ (data, domain, presentation)
│       │       ├── streak/
│       │       │   ├── data/StreakRepositoryImpl.kt
│       │       │   ├── domain/ (StreakRepository, StreakCalculator, model/)
│       │       │   └── presentation/ (StreakScreen, StreakViewModel, components/)
│       │       ├── settings/ (data, domain, presentation)
│       │       └── background/ (model, presentation)
│       ├── androidMain/
│       │   ├── kotlin/ (platform actuals: SoundManager, NotificationManager, Rive, getCurrentDate)
│       │   └── res/raw/ (muza_cat.riv, fire.riv, star.riv)
│       ├── iosMain/kotlin/ (platform actuals placeholder)
│       ├── jvmMain/kotlin/ (Desktop platform actuals)
│       ├── commonTest/kotlin/ (test files)
│       └── wasmJsMain/kotlin/ (disabled)
├── gradle/
│   └── libs.versions.toml
└── build.gradle.kts
```

## Key Classes/Modules

### ViewModels
| Class | File | Responsibility |
|-------|------|----------------|
| AppViewModel | viewmodel/AppViewModel.kt | Unified pomodoro state: timer countdown, mode switching, tasks, workspace, ambient, music |
| StreakViewModel | streak/presentation/StreakViewModel.kt | Streak calculation + heat map state |

### Repositories
| Class | Responsibility |
|-------|----------------|
| UserAppStateRepositoryV2 | User settings (V2 schema via MultiplatformSettings) |
| StreakRepository | Streak history (JSON in MultiplatformSettings, max 365 days) |
| LearningSessionRepository | Session CRUD (SQLDelight), events, tasks |
| LearningSessionManager | Session lifecycle orchestration |
| AmbientSoundRepository | Ambient sound list |
| MusicRepository | Music track list |
| BackgroundRepository | Background options |

### Screens (Voyager)
| Class | Purpose |
|-------|---------|
| StartupLoadingScreen | Initial loading + route to onboarding or session history |
| OnboardingScreen | First-time setup (OnboardingView) |
| LearningStyleScreen | Solo/Group selection |
| SessionHistoryScreen | View past learning sessions, create new one |
| MainTabScreen | Bottom nav container: Timer / Streak / Settings |
| StreakScreen | Standalone Voyager screen wrapping StreakScreenContent |
| PomodoroScreenV2 | Full workspace (timer + tasks + ambient + music) |
| SettingsScreen | User preferences |

## Design Patterns
- **MVVM**: ViewModel + StateFlow → Compose UI
- **Repository Pattern**: Data abstraction via interfaces (StreakRepository, LearningSessionRepository)
- **Dependency Registry**: Manual DI via singleton object (DependencyRegistry)
- **Feature-based packaging**: Each feature has domain/data/presentation layers
- **Platform expect/actual**: SoundManager, NotificationManager, RivePetView, getCurrentDate
- **Sealed interface navigation**: AuraScreen sealed interface for type-safe routes
- **Voyager Navigator**: Screen-based navigation with FadeTransition

## Critical Code Patterns

### Streak Integration
- `AppViewModel` calls `DependencyRegistry.streakRepository.incrementToday()` when a pomodoro work session completes
- `StreakViewModel` observes `StreakRepository.getHistoryFlow()` and recalculates via `StreakCalculator`
- `StreakCalculator` is a pure object with no dependencies (fully testable)

### Rive Animation Integration
- `RivePetView`, `RiveFireIcon`, `RiveStarIcon` declared as `expect` composables in commonMain
- Android actual uses `app.rive.runtime.kotlin.compose` API loading from `R.raw.*`
- iOS/JVM actuals are placeholders (Text fallback)
