# Business Overview

## Business Context
Ứng dụng Pomodoro đa nền tảng (Android, iOS, Desktop, Web) giúp người dùng quản lý thời gian học tập/làm việc theo phương pháp Pomodoro, kết hợp với tính năng nhóm học tập (group learning).

## Business Description
- **Mục đích chính**: Hỗ trợ người dùng tập trung làm việc/học tập bằng kỹ thuật Pomodoro (25 phút làm việc - 5 phút nghỉ)
- **Đối tượng**: Sinh viên, người đi làm cần quản lý thời gian hiệu quả
- **Mô hình**: Ứng dụng cá nhân + tính năng nhóm (group learning)

## Business Transactions
1. **Pomodoro Session**: Người dùng bắt đầu, tạm dừng, hoàn thành phiên Pomodoro
2. **Task Management**: Tạo, sửa, xóa, hoàn thành task gắn với phiên Pomodoro
3. **Settings Management**: Cấu hình thời gian work/break, background, âm thanh
4. **Onboarding Flow**: Hướng dẫn người dùng mới setup ứng dụng
5. **Learning Style Selection**: Chọn phong cách học (solo/group)
6. **Group Learning**: Tham gia nhóm học tập với chat, members panel
7. **Statistics Tracking**: Theo dõi số pomodoro hoàn thành hàng ngày
8. **Background & Ambient**: Chọn hình nền và âm thanh môi trường khi làm việc

## Business Dictionary
| Term | Meaning |
|------|---------|
| Pomodoro | Phiên làm việc tập trung (mặc định 25 phút) |
| Break | Thời gian nghỉ ngắn giữa các phiên (mặc định 5 phút) |
| Long Break | Thời gian nghỉ dài sau 4 phiên (mặc định 15 phút) |
| Task | Công việc cần hoàn thành trong phiên Pomodoro |
| Session | Bản ghi một phiên Pomodoro đã hoàn thành |
| DailyStats | Thống kê số pomodoro hoàn thành trong ngày |
| Learning Style | Phong cách học: Solo hoặc Group |
| Workspace | Màn hình chính chứa timer, tasks, settings |
| Ambient Sound | Âm thanh nền (mưa, sóng biển...) hỗ trợ tập trung |
| Compact Mode | Chế độ thu gọn UI trên màn hình nhỏ |

## Component Level Business Descriptions
### Pomodoro Timer
- **Purpose**: Đếm ngược thời gian phiên Pomodoro
- **Responsibilities**: Start/pause/reset timer, chuyển mode (work/break), thông báo khi hết giờ

### Task Management
- **Purpose**: Quản lý danh sách công việc
- **Responsibilities**: CRUD tasks, đánh dấu hoàn thành, hiển thị task hiện tại

### Settings
- **Purpose**: Cấu hình cá nhân hóa ứng dụng
- **Responsibilities**: Lưu/đọc settings (thời gian, background, sound), đồng bộ settings

### Startup & Onboarding
- **Purpose**: Khởi động app và hướng dẫn người dùng mới
- **Responsibilities**: Kiểm tra trạng thái onboarding, điều hướng đến màn hình phù hợp

### Learning Mode
- **Purpose**: Hỗ trợ học nhóm
- **Responsibilities**: Chọn learning style, quản lý group config, chat, members panel

### Background & Ambient
- **Purpose**: Tạo không gian làm việc thoải mái
- **Responsibilities**: Hiển thị dynamic background, phát ambient sounds, music
