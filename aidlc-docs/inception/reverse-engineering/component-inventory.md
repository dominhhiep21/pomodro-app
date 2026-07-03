# Component Inventory

## Application Modules
| Module | Purpose |
|--------|---------|
| androidApp | Android entry point (MainActivity + Compose) |
| desktopApp | Desktop JVM entry (main.kt + Window) |
| iosApp | iOS entry (SwiftUI + ComposeView) - planned |
| webApp | Web entry (disabled, Wasm/JS target) |

## Shared Module - Core
| Package | Purpose |
|---------|---------|
| core/config | AppConfig constants (server URL, default work/break minutes) |
| core/designsystem/components | GlassBox, AuraButton, AuraInputField, AuraDialog, AuraHeader, AuraCheckbox, AuraCircularProgress, AuraBackground, AuraScrollbar, AuraNavBar |
| core/designsystem/theme | AuraColors, AuraGradients, AuraAnimations, AuraTheme, AuraTypography, AuraShapes, BreathingEffect |
| core/media | SoundManager interface (expect/actual) |
| core/navigation | AuraScreen sealed interface, ScreenNavigation |
| core/notification | NotificationManager interface (expect/actual) |
| core/pomodoro/mini_client | KtorPomodoroMiniClient, Models (request/response DTOs) |
| core/utils | DateUtils, ScrollUtils, TimeFormatter, InlineFunctionUtil |

## Shared Module - Features
| Package | Purpose |
|---------|---------|
| features/main | MainTabScreen - Bottom navigation container (Timer/Streak/Settings) |
| features/startup | App loading, StartupLoadingScreen, route logic |
| features/onboarding | OnboardingScreen, OnboardingView (multi-step intro) |
| features/learning/mode | LearningStyle selection (Solo/Group), LearningStyleScreen, domain models |
| features/pomodoro/_base | PomodoroScreenV2, domain models, repositories (UserAppStateRepositoryV2) |
| features/pomodoro/viewmodel | AppViewModel (unified state), TimerUiState, WorkspaceUiState, TasksUiState |
| features/pomodoro/timer | Timer domain + presentation (TimerCircleComponent) |
| features/pomodoro/task | Task management components + domain |
| features/pomodoro/ambient | Ambient sound data + domain + presentation |
| features/pomodoro/music | Music playback data + domain + presentation |
| features/session | Learning session CRUD (data: Repository+Manager, domain: models+enums, presentation: SessionHistoryScreen) |
| features/streak | Streak tracking (data: StreakRepositoryImpl, domain: StreakCalculator+models, presentation: StreakScreen+ViewModel+components) |
| features/settings | Settings management (data: BackgroundRepository, domain: AppBackground, presentation: SettingsScreen+ViewModel) |
| features/background | Dynamic backgrounds (model: BackgroundModels, presentation: DynamicBackground+components) |

## Shared Module - DI
| Package | Purpose |
|---------|---------|
| di | DependencyRegistry (manual singleton DI - provides repositories, managers, clients) |

## Shared Module - Database
| Package | Purpose |
|---------|---------|
| database | DatabaseDriverFactory (expect/actual), AuraDatabase schema (SQLDelight) |

## Platform-Specific Code

### androidMain
- SoundManager actual (MediaPlayer)
- NotificationManager actual
- RivePetView, RiveFireIcon, RiveStarIcon actuals (Rive Compose API)
- getCurrentDate() actual
- `res/raw/`: muza_cat.riv, fire.riv, star.riv

### jvmMain (Desktop)
- SoundManager actual (JLayer MP3)
- NotificationManager actual (stub)
- RivePetView, RiveFireIcon, RiveStarIcon actuals (Text fallback)
- getCurrentDate() actual

### iosMain
- SoundManager actual (placeholder)
- NotificationManager actual (placeholder)
- RivePetView, RiveFireIcon, RiveStarIcon actuals (placeholder)
- getCurrentDate() actual

## Total Count
- **Total Modules**: 5 (4 platform + 1 shared)
- **Active Modules**: 4 (webApp disabled)
- **Feature Packages**: 9 (main, startup, onboarding, learning, pomodoro, session, streak, settings, background)
- **Core Packages**: 7
- **Design System Components**: 10+ composables
- **Kotlin Source Files**: ~120+
