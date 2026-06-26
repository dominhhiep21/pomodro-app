# Component Inventory

## Application Modules
| Module | Purpose |
|--------|---------|
| androidApp | Android entry point (MainActivity + Compose) |
| desktopApp | Desktop JVM entry (main.kt + Window) |
| iosApp | iOS entry (SwiftUI + ComposeView) |
| webApp | Web entry (disabled, Wasm/JS target) |

## Shared Module - Core
| Package | Purpose |
|---------|---------|
| core/config | AppConfig constants |
| core/designsystem/components | GlassBox, AuraButton, AuraInputField |
| core/designsystem/theme | AuraColors, AuraGradients, AuraAnimations, BreathingEffect |
| core/media | SoundManager interface |
| core/navigation | AuraScreen, AuraNavigator |
| core/notification | NotificationManager interface |
| core/pomodoro/mini_client | KtorPomodoroMiniClient, Models |
| core/utils | DateUtils, ScrollUtils, TimeFormatter |

## Shared Module - Features
| Package | Purpose |
|---------|---------|
| features/startup | App loading, StartupViewModel, StartupRepository |
| features/onboarding | OnboardingScreen, OnboardingStep |
| features/learning/mode | LearningStyle, LearningGroupConfig, LearningStyleScreen |
| features/pomodoro/_base | PomodoroScreenV2, domain models, repositories |
| features/pomodoro/viewmodel | TimerViewModel, TasksViewModel, WorkspaceViewModel |
| features/pomodoro/timer | TimerCircleComponent, TimerSizes |
| features/pomodoro/task | TaskSection, TaskSideBar, TaskBottomBar, TaskItem |
| features/pomodoro/ambient | AmbientSound, AmbientSoundRepository, AmbientSoundSection |
| features/pomodoro/music | MusicTrack, MusicRepository, MusicSection |
| features/settings | SettingsScreen, SettingsViewModel, BackgroundRepository |
| features/background | DynamicBackground, BackgroundModels, animated components |

## Shared Module - DI
| Package | Purpose |
|---------|---------|
| di | DependencyRegistry (manual singleton DI) |

## Total Count
- **Total Modules**: 5 (4 platform + 1 shared)
- **Active Modules**: 4 (webApp disabled)
- **Feature Packages**: 7
- **Core Packages**: 7
- **Kotlin Files**: 116
