# Technology Stack

## Programming Languages
| Language | Version | Usage |
|----------|---------|-------|
| Kotlin | 2.3.21 | Primary (all shared + platform code) |
| Swift | N/A | iOS entry point only |

## Frameworks
| Framework | Version | Purpose |
|-----------|---------|---------|
| Kotlin Multiplatform | 2.3.21 | Cross-platform shared code |
| Compose Multiplatform | 1.11.0 | Declarative UI framework |
| Voyager | 2.2.21-1.10.3 | Multiplatform navigation |
| Ktor Client | 3.0.1 | HTTP networking |
| Kotlinx Serialization | 1.8.0 | JSON serialization |
| Kotlinx Datetime | 0.7.1 | Date/time utilities |
| MultiplatformSettings | 1.3.0 | Key-value local storage |
| AndroidX Lifecycle | 2.11.0-beta01 | ViewModel + Lifecycle |
| Rive Android | 11.7.1 | Rive animation runtime (Android) |
| SQLDelight | 2.3.2 | Type-safe SQL database |

## Infrastructure
| Service | Purpose |
|---------|---------|
| Pomodoro Mini Server | Backend API (external) |
| MultiplatformSettings | Local persistent storage |
| SQLDelight (AuraDatabase) | Local relational DB for sessions/tasks/events |

## Build Tools
| Tool | Version | Purpose |
|------|---------|---------|
| Gradle | via wrapper | Build system |
| AGP | 9.0.1 | Android Gradle Plugin |
| Kotlin Compiler Plugin | 2.3.21 | Compose compiler |

## Testing Tools
| Tool | Version | Purpose |
|------|---------|---------|
| kotlin-test | 2.3.21 | Unit testing |
| kotlinx-coroutines-test | 1.11.0 | Coroutine testing |

## Platform Targets
| Platform | Engine | Status |
|----------|--------|--------|
| Android | Compose + CIO | Active |
| Desktop (JVM) | Compose + CIO + JLayer | Active |
| iOS | Compose + Darwin | Planned |
| Web (Wasm/JS) | Compose | Disabled |
