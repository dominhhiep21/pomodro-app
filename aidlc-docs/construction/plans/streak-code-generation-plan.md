# Code Generation Plan - Streak Feature

## Unit Context
- **Unit**: Streak Screen with Rive Pet
- **Stories**: FR-1 (Streak Calc), FR-2 (Heat Map), FR-3 (Pet Rive), FR-4 (Navigation), FR-5 (Storage)
- **Platform**: Android + iOS (Rive), commonMain (streak logic + UI)

---

## Step 1: Add Dependencies
- [x] Add `rive-android` to `libs.versions.toml` and `androidApp/build.gradle.kts`
- [x] Add `androidx.startup:startup-runtime` dependency
- [x] Add Rive initialization to `AndroidManifest.xml`

## Step 2: Place .riv File
- [x] Copy `27136-51126-cat-pomodoro.riv` to `androidApp/src/main/res/raw/cat_pomodoro.riv`
- [x] Note: iOS will load from bundle (configured later)

## Step 3: Data Layer - StreakRepository
- [x] Create `features/streak/data/StreakRepository.kt` (interface in domain)
- [x] Create `features/streak/data/StreakRepositoryImpl.kt` using MultiplatformSettings
- [x] Implement: save daily stats history (JSON list), get streak history, calculate streaks
- [x] Register in `DependencyRegistry`

## Step 4: Domain Layer - Streak Calculation
- [x] Create `features/streak/domain/model/StreakData.kt` (current streak, longest streak, daily history)
- [x] Create `features/streak/domain/StreakCalculator.kt` (pure function: List<DailyStats> → StreakData)

## Step 5: Presentation Layer - StreakViewModel
- [x] Create `features/streak/presentation/StreakViewModel.kt`
- [x] Expose: `StateFlow<StreakUiState>` (current streak, longest, heat map data, pomodoro state)
- [x] Connect to StreakRepository + UserAppStateRepository (for pomodoro time state)

## Step 6: UI - Calendar Heat Map Composable
- [x] Create `features/streak/presentation/components/CalendarHeatMap.kt`
- [x] GitHub-style grid: 7 rows (days) × N columns (weeks)
- [x] 5 intensity levels using AuraColors primary

## Step 7: UI - Streak Counter Cards
- [x] Create `features/streak/presentation/components/StreakCounterCard.kt`
- [x] GlassBox cards showing current streak + longest streak
- [ ] **KHÔNG dùng emoji** (🔥⭐) — dùng Rive animated icons:
  - Current streak: `612-1196-fire.riv` → artboard `New Artboard`, animation `Fire9` (auto-loop)
  - Longest streak: `25021-46695-star.riv` → artboard `Artboard`, state machine `State Machine 1` (auto-loop)
  - Place cả 2 file vào `androidApp/src/main/res/raw/` (fire.riv, star.riv)
  - Sử dụng cùng expect/actual pattern như RivePetView nhưng nhỏ gọn hơn (~32dp icon size)
- [ ] Bounce animation on entry


## Step 8: Platform - Rive Pet Composable (expect/actual)
- [x] Create `features/streak/presentation/components/RivePetView.kt` in commonMain (expect)
- [x] Create Android actual in `androidMain` using `Rive()` Compose API
- [x] Create iOS actual in `iosMain` (placeholder/UIKit interop with rive-ios)
- [ ] State machine mapping (from .riv analysis):
  - **State Machine**: `State Machine 1`
  - **Artboard**: `Artboard`
  - **Pomodoro states**:
    - Not in session + streak=0: trigger `animSleepy` or `animSleep`
    - Not in session + streak>0: `animIdle` (idle loop + Blink)
    - Pomodoro 0-33%: `animFocusLvl1` (Fokus_lvl_1)
    - Pomodoro 33-66%: `animFocusLvl2` (Fokus_lvl_2)
    - Pomodoro 66-100%: `animFocusLvl3` (Fokus_lvl_3)
    - Focus ending: `animaFocusEnd`
    - Break time: `Break9`
    - Break ending: `Break Ending9`
  - **Tap interaction**: fire `klik` trigger → food interaction (`foodAreaHit`, `eatingCatFood`)
  - **Inputs to drive**:
    - `Boolean 1` / `isbtmPressed`: button/tap state
    - `idle_gate`: gate idle animation
    - `speed`: animation speed control
    - `totalEatingFish`: counter for eating interactions
    - `fishSpawn_off`: toggle food spawn
  - **App launch**: `App Launch9` animation for first-open celebration
  - **Streak milestone celebration**: Use `App Launch9` or transition to eating happy state

## Step 9: UI - StreakScreen (Main Screen)
- [x] Create `features/streak/presentation/StreakScreen.kt` (Voyager Screen)
- [x] Layout: Pet (top 40%) → Streak cards (middle) → Heat map (bottom)
- [x] Apply AuraColors/GlassBox design system

## Step 10: Navigation - Bottom Navigation
- [x] Modify existing navigation to add bottom nav with Timer/Streak/Settings
- [x] Add `StreakScreen` as Voyager Screen destination

## Step 11: Integration - Connect Pomodoro Timer to Streak
- [x] Hook `TimerViewModel` completed session → update StreakRepository daily stats
- [x] Ensure streak data updates when pomodoro session completes

## Step 12: iOS Rive Setup (Placeholder)
- [x] Create expect/actual structure for iOS Rive loading
- [x] Document Swift package setup instructions for `rive-ios`

---

## Dependencies Between Steps
```
Step 1,2 → Step 8 (Rive deps needed for pet view)
Step 3,4 → Step 5 (Repository + Calculator needed for ViewModel)
Step 5 → Step 6,7,9 (ViewModel needed for UI)
Step 8 → Step 9 (Pet composable needed for main screen)
Step 9 → Step 10 (Screen needed for navigation)
Step 5,11 → Integration (ViewModel + timer hook)
```

## Execution Order
1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 12
