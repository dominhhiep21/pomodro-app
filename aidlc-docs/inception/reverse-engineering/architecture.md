# System Architecture

## System Overview
Kotlin Multiplatform application sử dụng Compose Multiplatform cho UI, hỗ trợ Android, Desktop (JVM), iOS (planned). Kiến trúc theo pattern MVVM với feature-based modular structure. App sử dụng Voyager cho navigation, SQLDelight cho database persistence, và MultiplatformSettings cho local key-value storage.

## Architecture Diagram

```mermaid
graph TD
    subgraph Platforms["Platform Entry Points"]
        Android["androidApp"]
        Desktop["desktopApp"]
        iOS["iosApp (planned)"]
    end

    subgraph Shared["shared module (commonMain)"]
        subgraph Core["core/"]
            Config["config/AppConfig"]
            DesignSystem["designsystem/"]
            Media["media/SoundManager"]
            Navigation["navigation/ScreenNavigation"]
            Notification["notification/NotificationManager"]
            Utils["utils/"]
            MiniClient["pomodoro/mini_client/KtorPomodoroMiniClient"]
        end
        
        subgraph Features["features/"]
            Startup["startup/"]
            Onboarding["onboarding/"]
            Learning["learning/mode/"]
            Pomodoro["pomodoro/"]
            Session["session/"]
            Streak["streak/"]
            Settings["settings/"]
            Background["background/"]
            MainTab["main/MainTabScreen"]
        end
        
        subgraph DI["di/"]
            Registry["DependencyRegistry"]
        end

        subgraph Database["database/"]
            AuraDB["AuraDatabase (SQLDelight)"]
        end
    end

    Android --> Shared
    Desktop --> Shared
    iOS --> Shared
    
    Startup --> Navigation
    Onboarding --> Navigation
    Learning --> Navigation
    Pomodoro --> Core
    Session --> Core
    Streak --> Core
    Settings --> Core
    MainTab --> Pomodoro
    MainTab --> Streak
    MainTab --> Settings
```

### Text Alternative
```
Platform Entry Points: androidApp, desktopApp, iosApp → shared module
shared module:
  core/ → config, designsystem, media, navigation, notification, utils, pomodoro mini_client
  features/ → startup, onboarding, learning, pomodoro, session, streak, settings, background, main
  di/ → DependencyRegistry
  database/ → AuraDatabase (SQLDelight)
```

## Component Descriptions

### Platform Modules
| Module | Purpose | Type |
|--------|---------|------|
| androidApp | Android entry point (MainActivity + Compose) | Application |
| desktopApp | Desktop JVM entry point (main.kt) | Application |
| iosApp | iOS entry point (SwiftUI) - planned | Application |
| webApp | Web entry point (disabled in settings.gradle.kts) | Application |
| shared | Shared business logic + UI | Library |

### Core Layer
| Component | Purpose |
|-----------|---------|
| AppConfig | Server URL, app configuration constants (work/break minutes defaults) |
| DesignSystem | AuraColors, AuraTheme, AuraGradients, GlassBox, AuraButton, AuraDialog, BreathingEffect |
| SoundManager | Platform-agnostic sound playback interface (expect/actual) |
| ScreenNavigation | AuraScreen sealed interface + AuraNavigator |
| NotificationManager | Platform-agnostic notification interface (expect/actual) |
| KtorPomodoroMiniClient | HTTP client for Pomodoro Mini server API (register, login, settings, tasks, logs) |

### Feature Layer
| Feature | Purpose |
|---------|---------|
| startup | App loading, determine next destination based on onboarding state |
| onboarding | First-time user setup flow (OnboardingView) |
| learning/mode | Learning style selection (Solo/Group) + group configuration |
| pomodoro | Main workspace: timer, tasks, music, ambient (AppViewModel manages all) |
| session | Learning session lifecycle: create, persist (SQLDelight), history screen |
| streak | Streak calculation + calendar heat map + Rive pet animation |
| settings | User preferences management + background selection |
| background | Dynamic animated backgrounds |
| main | MainTabScreen: bottom navigation container (Timer/Streak/Settings tabs) |

## Data Flow
```mermaid
sequenceDiagram
    participant User
    participant UI as Compose UI
    participant VM as ViewModel
    participant Repo as Repository
    participant DB as SQLDelight
    participant KV as MultiplatformSettings

    User->>UI: Interact (start timer, tap pet, etc.)
    UI->>VM: Action
    VM->>Repo: Read/Write data
    Repo->>DB: Session history, tasks, events
    Repo->>KV: Settings, streak history (JSON)
    DB-->>Repo: Query results
    KV-->>Repo: Key-value data
    Repo-->>VM: State update
    VM-->>UI: StateFlow emission
    UI-->>User: UI Update
```

## Integration Points
- **Network API**: KtorPomodoroMiniClient → Pomodoro Mini Server (register, login, tasks, settings, pomodoro logs)
- **Local Database**: SQLDelight AuraDatabase → Session history, learning events, tasks
- **Local Storage**: MultiplatformSettings (key-value) → User settings, streak history (JSON), onboarding state
- **Platform Services**: SoundManager, NotificationManager (platform-specific expect/actual implementations)
- **Rive Animation**: Platform-specific Rive runtime (rive-android for Android, rive-ios planned for iOS)

## Navigation Flow
```
StartupLoadingScreen
  ├── (onboarding not completed) → OnboardingScreen → LearningStyleScreen → MainTabScreen
  └── (onboarding completed) → SessionHistoryScreen → LearningStyleScreen → MainTabScreen

MainTabScreen (Bottom Navigation)
  ├── Tab 0: PomodoroScreenUIv2 (Timer + Tasks + Ambient + Music)
  ├── Tab 1: StreakScreenContent (Pet + Streak Cards + Heat Map)
  └── Tab 2: Settings (placeholder)
```
