# Streak Screen - Requirements Verification Questions

Vui lòng trả lời các câu hỏi dưới đây bằng cách điền letter choice sau tag [Answer]:

---

## Question 1
Streak screen sẽ hiển thị loại thông tin streak nào?

A) Streak ngày liên tiếp hoàn thành ít nhất 1 pomodoro
B) Streak ngày liên tiếp hoàn thành tất cả tasks
C) Streak ngày liên tiếp đạt target pomodoro (ví dụ: 4 pomodoro/ngày)
D) Kết hợp nhiều loại streak (daily, weekly, longest)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Question 2
Pet cat trong file .riv sẽ tương tác với streak data như thế nào?

A) Pet có các trạng thái animation khác nhau dựa theo streak level (ví dụ: ngủ khi streak = 0, vui khi streak cao)
B) Pet chỉ là decoration animation đơn giản, không phản ứng theo streak
C) Pet có animation phản hồi khi user tap/interact (ngoài streak)
D) Pet thay đổi appearance/outfit dựa trên milestones (streak 7, 30, 100...)
E) Other (please describe after [Answer]: tag below)

[Answer]: E - Pet sẽ thay đổi tùy theo thời gian trong một pomodoro và cos thể tương tác khi người dùng bấm vào

---

## Question 3
Streak screen sẽ được truy cập từ đâu trong app?

A) Tab/button trên main PomodoroScreenV2
B) Mục trong Settings screen
C) Bottom navigation item mới
D) Một button trên timer area
E) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Question 4
Về platform support cho Rive animation - `rive-android` chỉ hỗ trợ Android. Bạn muốn xử lý các platform khác như thế nào?

A) Chỉ hiển thị Rive pet animation trên Android, các platform khác hiển thị static image thay thế
B) Chỉ hỗ trợ Android cho feature này (skip iOS/Desktop)
C) Tìm giải pháp multiplatform (rive-cmp wrapper hoặc expect/actual)
D) Hiển thị Rive trên Android + iOS (dùng rive-ios cho iOS riêng), static image cho Desktop
E) Other (please describe after [Answer]: tag below)

[Answer]: E - chỉ thực hện trên android và ios

---

## Question 5
Streak data sẽ được lưu trữ ở đâu?

A) Local storage (MultiplatformSettings) - tính toán streak từ existing DailyStats
B) Thêm fields mới vào UserAppStateRepository
C) Tạo repository riêng cho streak data
D) Sync với server (KtorPomodoroMiniClient)
E) Other (please describe after [Answer]: tag below)

[Answer]: E - Cái này project đang như thế nào và đang hỗ trợ gì ?

---

## Question 6
Những thông tin nào sẽ hiển thị trên Streak screen ngoài pet animation?

A) Chỉ current streak + pet animation
B) Current streak + longest streak + calendar heat map
C) Current streak + longest streak + daily history (tuần gần nhất)
D) Current streak + milestones/badges + pet status
E) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Question 7
Pet animation trong file .riv có sử dụng state machine không? (Nếu có, bạn biết tên các state/input nào?)

A) Có state machine - tôi biết tên các states (liệt kê sau [Answer]: tag)
B) Có state machine - tôi không biết tên, cần AI phân tích file .riv
C) Không có state machine - chỉ là timeline animation đơn giản
D) Tôi không chắc - cần kiểm tra file .riv
E) Other (please describe after [Answer]: tag below)

[Answer]: D

---

## Question 8
UI design cho Streak screen?

A) Theo design system hiện có (AuraColors, GlassBox, AuraTheme) - tông dark/glass
B) Design mới riêng cho streak screen (tông sáng, celebratory)
C) Kết hợp design hiện có + elements mới cho streak milestones
D) Minimalist - focus vào pet animation chiếm phần lớn screen
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---
