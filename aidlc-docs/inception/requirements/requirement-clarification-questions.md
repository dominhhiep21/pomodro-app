# Streak Screen - Clarification Questions

Tôi phát hiện một số điểm cần làm rõ từ câu trả lời của bạn:

---

## Clarification 1: Pet Animation Behavior (Q2)

Bạn nói "Pet sẽ thay đổi tùy theo thời gian trong một pomodoro và có thể tương tác khi người dùng bấm vào".

Nhưng **Streak screen** và **Pomodoro timer screen** là 2 màn hình khác nhau. Bạn muốn:

A) Pet animation chỉ xuất hiện trên Streak screen - phản ứng theo streak level + tap interaction
B) Pet animation xuất hiện trên CẢ hai màn hình: trên Timer screen thay đổi theo thời gian pomodoro, trên Streak screen phản ứng theo streak
C) Pet chỉ xuất hiện trên Timer screen (thay đổi theo thời gian pomodoro + tap), và Streak screen KHÔNG có pet
D) Other (please describe after [Answer]: tag below)

[Answer]: D - Pet animation chỉ xuất hiện trên Streak screen - phản ứng theo streak level + tap interaction, nếu trong thời gian pomodoro, nó sẽ biểu thị những trạng thái tương ứng với thời gian

---

## Clarification 2: Storage Strategy (Q5)

Sau khi phân tích code hiện tại, tôi phát hiện:

**Hạn chế hiện tại**: `LocalSettingsDataSource` chỉ lưu DailyStats cho **1 ngày duy nhất** (ngày hiện tại). Không có lịch sử nhiều ngày.

**Để tính streak + calendar heat map**, cần lưu lịch sử nhiều ngày. Có 2 phương án:

A) Mở rộng MultiplatformSettings - lưu List<DailyStats> (JSON array) cho nhiều ngày (đơn giản, giới hạn ~365 ngày)
B) Tạo StreakRepository riêng dùng MultiplatformSettings - tách biệt với UserAppStateRepository, lưu streak history
C) Kích hoạt lại SQLDelight (đã comment out trong project) - dùng database cho lịch sử lớn
D) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Clarification 3: iOS Implementation (Q4)

Bạn chọn "chỉ thực hiện trên Android và iOS". Với Rive trên iOS, có các option:

A) Dùng `rive-ios` native (Swift package) + expect/actual pattern trong KMP - iOS code riêng trong iosMain
B) Dùng library `rive-cmp` (community wrapper: `dev.muazkadan:rive-cmp`) - hỗ trợ cả Android+iOS trong commonMain
C) Chỉ implement Rive trên Android trước, iOS để sau (phase 2)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---
