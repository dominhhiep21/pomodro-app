# Requirements - Streak Screen with Rive Pet

## Intent Analysis
- **User Request**: Thêm màn hình Streak với pet animation từ file `27136-51126-cat-pomodoro.riv`
- **Request Type**: New Feature
- **Scope**: Multiple Components (new screen, new navigation, new repository, platform-specific Rive integration)
- **Complexity**: Moderate

---

## Functional Requirements

### FR-1: Streak Calculation
- Tính streak dựa trên **ngày liên tiếp hoàn thành ít nhất 1 pomodoro**
- Hiển thị **current streak** (số ngày liên tiếp hiện tại)
- Hiển thị **longest streak** (kỷ lục streak dài nhất)
- Streak bị reset khi 1 ngày không hoàn thành bất kỳ pomodoro nào

### FR-2: Calendar Heat Map
- Hiển thị heat map dạng calendar (tương tự GitHub contributions)
- Mỗi ngày có màu intensity dựa trên số pomodoro hoàn thành
- Hiển thị ít nhất vài tuần gần nhất

### FR-3: Pet Animation (Rive)
- Hiển thị pet cat animation từ file `.riv` trên Streak screen
- Pet **phản ứng theo streak level** (ví dụ: trạng thái khác nhau khi streak thấp/cao)
- Pet **phản ứng theo thời gian pomodoro hiện tại** (nếu đang trong phiên pomodoro, biểu thị trạng thái tương ứng)
- User có thể **tap vào pet** để trigger tương tác animation
- Cần phân tích file `.riv` để xác định state machine inputs/animations có sẵn

### FR-4: Navigation
- Thêm **Bottom navigation item mới** để truy cập Streak screen
- Tích hợp vào navigation hiện có (Voyager)

### FR-5: Data Storage
- Tạo **StreakRepository riêng** dùng MultiplatformSettings
- Tách biệt khỏi UserAppStateRepository hiện có
- Lưu lịch sử DailyStats nhiều ngày (cho streak calculation + heat map)

---

## Non-Functional Requirements

### NFR-1: Platform Support
- **Android**: Rive animation via `app.rive:rive-android` (Compose API)
- **iOS**: Rive animation via `rive-ios` native (Swift package) + expect/actual pattern
- **Desktop/Web**: Không hỗ trợ feature này (hoặc hiển thị fallback nếu cần)

### NFR-2: UI Design
- Theo design system hiện có: **AuraColors, GlassBox, AuraTheme** (tông dark/glass)
- Consistent với phong cách UI của app hiện tại

### NFR-3: Performance
- Rive animation không được ảnh hưởng đến performance timer
- Streak calculation phải nhanh (local computation)

---

## Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Rive Android API | New Compose API (`Rive()` composable) | Production-ready, native Compose support |
| Rive iOS | `rive-ios` native + expect/actual | Official iOS runtime, best performance |
| Storage | StreakRepository riêng (MultiplatformSettings) | Tách biệt concerns, đơn giản |
| Navigation | Bottom navigation item mới | Dễ truy cập, user-friendly |
| Design | AuraColors/GlassBox existing system | Consistency |

---

## UI Design Guidelines

### Design Direction: Dark Glass (Existing System)
- **Theme**: AuraColors + AuraTheme (dark background, glass effects)
- **Components**: GlassBox containers, AuraButton, AuraGradients
- **Style**: Glassmorphism - semi-transparent surfaces with blur, subtle borders

### Screen Layout Structure
```
┌─────────────────────────────────┐
│  StreakScreen                    │
├─────────────────────────────────┤
│                                 │
│   ┌─────────────────────────┐   │
│   │   Rive Pet Animation    │   │
│   │   (chiếm ~40% screen)  │   │
│   │   Tap to interact       │   │
│   └─────────────────────────┘   │
│                                 │
│   ┌──────────┐ ┌──────────┐    │
│   │ Current  │ │ Longest  │    │
│   │ Streak   │ │ Streak   │    │
│   │  🔥 7    │ │  ⭐ 23   │    │
│   └──────────┘ └──────────┘    │
│                                 │
│   ┌─────────────────────────┐   │
│   │  Calendar Heat Map      │   │
│   │  (GitHub-style grid)    │   │
│   │  ░░▓▓██░░▓▓██░░▓▓██   │   │
│   └─────────────────────────┘   │
│                                 │
├─────────────────────────────────┤
│  [Timer] [Streak🐱] [Settings] │  ← Bottom Nav
└─────────────────────────────────┘
```

### UI Design References

| Aspect | Reference | Áp dụng |
|--------|-----------|---------|
| Streak Counter | Duolingo streak system | Fire icon + số ngày, celebratory khi milestone |
| Heat Map | GitHub contributions grid | 5 mức intensity từ transparent → accent color |
| Pet Area | Pomodoro Kitty (Dribbble) | Pet chiếm không gian chính, center screen |
| Glass Cards | Existing GlassBox component | Semi-transparent cards cho streak stats |
| Dark Theme | Existing AuraColors | Deep dark background + accent glows |
| Animations | 60fps.design streak patterns | Bounce effect khi streak tăng, pulse glow |

### Color Palette (từ AuraColors hiện có)
- **Background**: Dark gradient (existing)
- **Heat Map Intensity Levels**:
  - Level 0 (no activity): `surface` color (barely visible)
  - Level 1 (1 pomodoro): `primary` at 20% opacity
  - Level 2 (2-3 pomodoro): `primary` at 50% opacity
  - Level 3 (4-5 pomodoro): `primary` at 80% opacity
  - Level 4 (6+ pomodoro): `primary` full + glow effect
- **Streak Counter**: Accent color with custom animated flame/star icons (NO emoji)
- **Pet Area**: Floating glass container with subtle shadow

### Micro-animations
- Streak number **bounces** khi user mở screen (nếu streak > yesterday)
- Heat map cells **fade in** sequentially (left to right)
- Pet **idle animation** loop khi không tương tác
- Streak milestone (7, 30, 100) → **particle/confetti** burst

### Accessibility
- All icons có `contentDescription`
- Streak numbers đủ lớn (≥ 32sp)
- Heat map cells có tooltip khi long-press (ngày + số pomodoro)
- Sufficient contrast ratio trên dark background

---

## File .riv Analysis (Pending)
- File: `27136-51126-cat-pomodoro.riv` (tại project root)
- Cần phân tích: artboards, state machines, inputs
- Sẽ thực hiện trong Construction phase

---

## Dependencies

### New Dependencies Needed
- `app.rive:rive-android:11.7.1` (androidMain)
- `androidx.startup:startup-runtime:1.1.1` (for Rive initialization)
- `rive-ios` Swift package (iOS project)

### Existing Dependencies Used
- MultiplatformSettings (streak data storage)
- Voyager (navigation)
- Compose Multiplatform (UI)
- Compose Material 3 (theming, components)
- Compose Resources (icons, strings)
- Kotlinx Serialization (data serialization)
