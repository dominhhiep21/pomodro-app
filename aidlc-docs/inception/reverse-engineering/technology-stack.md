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
| Voyager | 2.2.21 | Multiplatform navigation |
| Ktor Client | 3.0.1 | HTTP networking |
| Kotlinx Serialization | 1.8.0 | JSON serialization |
| Multiplatform Settings | 1.3.0 | Key-value local storage |
| AndroidX Lifecycle | 2.11.0-beta01 | ViewModel + Lifecycle |

## Infrastructure
| Service | Purpose |
|---------|---------|
| Pomodoro Mini Server | Backend API (external) |
| MultiplatformSettings | Local persistent storage |
| SQLDelight (commented out) | Database (not active) |

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
| iOS | Compose + Darwin | Active |
| Web (Wasm) | Compose | Disabled |
| Web (JS) | Compose | Disabled |
