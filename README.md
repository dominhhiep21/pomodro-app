# Dự án Pomodoro Kotlin Multiplatform

Chào mừng bạn đến với dự án **pomodro-kotlin**! Đây là một ứng dụng quản lý thời gian theo phương
pháp Pomodoro, được xây dựng bằng công nghệ Kotlin Multiplatform (KMP), cho phép chia sẻ mã nguồn
tối đa giữa Android và Desktop.

---

## 1. Tổng quan Dự án (Project Overview)

* **Mục đích:** Cung cấp công cụ hỗ trợ tập trung công việc hiệu quả, chạy mượt mà trên nhiều thiết
  bị.
* **Công nghệ cốt lõi:**
    * **Kotlin Multiplatform (KMP):** Chia sẻ logic nghiệp vụ (business logic) dùng chung.
    * **Compose Multiplatform:** Xây dựng giao diện người dùng (UI) một lần và chạy ở mọi nơi.
* **Các nền tảng hỗ trợ (Targets):**
    * **Android:** Ứng dụng di động Android gốc.
    * **Desktop:** Ứng dụng máy tính chạy trên JVM (Windows, macOS, Linux).

---

## 2. Cấu trúc Codebase (Detailed Codebase Structure)

Dưới đây là sơ đồ chi tiết cách tổ chức các package và lớp bên trong module `:shared`:

```
commonMain
├── composeResources
└── kotlin
    └── thong.kotlin.pomodoro
        ├── core
        │   ├── config
        │   │   └── AppConfig.kt ---> Nơi thêm các config dùng chung cho App
        │   ├── designsystem
        │   │   ├── components ---> Các thành phần base UI của App, dùng để xây dựng UI của các Screen
        │   │   └── theme ---> Theme dùng chung của toàn App
        │   ├── media
        │   ├── notification
        │   ├── pomodoro.mini_client ---> Quản lí các API gọi dến Pomodoro Mini Server
        │   └── utils ---> Các hàm xử lý logic chung
        │
        ├── database ---> Xử lý Database SQLDelight
        │
        ├── di
        │   └── DependencyRegistry.kt ---> Tập hợp Singleton Objects
        │
        ├── features
        │   ├── background ---> UI và Tính năng background của App
        │   ├── learning.mode ---> UI và Tính năng của màn Chọn kiểu Learning
        │   ├── onboarding.presentation ---> UI và tính năng của màn Giới thiệu App
        │   ├── pomodoro
        │   │   ├── _base ---> Các thành phần base của tính năng học tập Pomodoro
        │   │   ├── ambient ---> Các thành phần và logic của Ambient sound
        │   │   ├── music ---> Các thành phần và logic của Âm nhạc
        │   │   ├── task ---> Các thành phần và logic của Nhiệm vụ
        │   │   ├── timer ---> Các thành phần và logic của Đồng hồ đếm ngược
        │   │   └── viewmodel ---> UI State của Feature
        │   ├── session ---> UI và tính năng của màn quản lý các phiên học tập
        │   ├── settings ---> UI và tính năng của màn Quản lý cấu hình
        │   └── startup ---> UI và tính năng của màn mở đầu App
        │
        ├── App.kt ---> Entrypoint của App
        ├── ExampleApp.kt ---> Đọc file này để hiểu thêm cách thiết kế App theo Voyager Navigator
        └── Platform.kt
```

### Chi tiết các thư mục:

* **`core/`**: Chứa các thành phần dùng chung cho toàn bộ ứng dụng như hệ thống giao diện (design
  system), điều hướng (navigation), cấu hình (config), và thông báo (notification).
* **`features/`**: Tổ chức theo tính năng. Mỗi thư mục con đại diện cho một mảng chức năng cụ thể
  của ứng dụng (Pomodoro logic, Cài đặt, Khởi động, v.v.).
* **`di/`**: Quản lý việc khởi tạo và cung cấp các phụ thuộc (Dependencies Injection).
* **`database/`**: Nơi dự kiến quản lý lưu trữ dữ liệu cục bộ.

---

## 3. Quy trình Phát triển & Chạy ứng dụng (Development Workflow)

Sử dụng các lệnh sau trong terminal để chạy ứng dụng:

* **Android:** `./gradlew :androidApp:assembleDebug`
* **Desktop:**
    * Chạy tiêu chuẩn: `./gradlew :desktopApp:run`
    * Hot reload (tự động cập nhật): `./gradlew :desktopApp:hotRun --auto`

---

## 4. Hướng dẫn Thêm Tính năng Mới (Adding a New Feature)

Để thêm một tính năng mới (ví dụ: "Thống kê"):

1. **Định nghĩa Logic:** Tạo package mới trong `shared/.../features/statistics/`.
2. **Tạo Screen bằng và quản lí bằng Voyager:**: Viết hàm Screen trong package vừa tạo
3. **Tạo Giao diện:** Viết các hàm `@Composable` bên trong package vừa tạo.
4. **Đăng ký Dependency:** Nếu cần, hãy đăng ký các lớp mới trong `DependencyRegistry.kt`.
5. **Cấu hình Điều hướng:** Thêm màn hình mới bằng bộ điều hướng Voyager Navigator.

Để thêm một base UI cho app:

1. **Vị trí:** Tạo file Kotlin mới trong thư mục `shared/.../core/designsystem/components/`.
2. **Đặt tên:** Sử dụng tiền tố `Aura` (ví dụ: `AuraCard.kt`, `AuraLoading.kt`) để đồng bộ với hệ
   thống Design System hiện tại.
3. **Triển khai:**
    * Sử dụng `@Composable` function.
    * Tận dụng các component nền tảng như `GlassBox` để giữ hiệu ứng đồng nhất.
    * Sử dụng `AuraTheme` để truy xuất màu sắc (`AuraTheme.colors`) và kiểu chữ (
      `AuraTheme.typography`).
4. **Ví dụ:**
    ```kotlin
    @Composable
    fun AuraNewComponent(modifier: Modifier = Modifier) {
        GlassBox(modifier = modifier) {
            Text("Nội dung", color = AuraTheme.colors.textPrimary)
        }
    }
    ```

Để thêm một config dùng chung cho toàn bộ app:

1. **Vị trí:** Mở tệp `shared/.../core/config/AppConfig.kt`.
2. **Triển khai:** Thêm các hằng số (`const val`) hoặc thuộc tính mới vào `object AppConfig`.
3. **Sử dụng:** Truy cập trực tiếp thông qua `AppConfig.YOUR_CONSTANT` ở bất kỳ đâu trong module
   `shared`.
4. **Ví dụ:**
    ```kotlin
    object AppConfig {
        const val NEW_SETTING = "Giá trị mặc định"
    }
    ```

Để thêm các hàm utilities cho logic dự án:

1. **Vị trí:** Tạo hoặc mở file Kotlin trong thư mục `shared/.../core/utils/`.
2. **Triển khai:**
    * Ưu tiên sử dụng **Extension Functions** để mở rộng tính năng cho các kiểu dữ liệu có sẵn.
    * Đặt tên file theo định dạng `[Tên]Utils.kt` hoặc mô tả chức năng của nó (ví dụ:
      `TimeFormatter.kt`).
3. **Ví dụ:**
    ```kotlin
    fun Long.toCustomFormat(): String {
        // Logic xử lý ở đây
        return "Kết quả"
    }
    ```

Để quản lí các API của Pomodoro Mini Server:

1. **Định nghĩa Models:** Thêm các data class với annotation `@Serializable` vào
   `shared/.../core/pomodoro/mini_client/Models.kt`.
2. **Khai báo Interface:** Thêm hàm mới vào interface `PomodoroMiniClient`.
3. **Triển khai Logic:** Thực hiện gọi API trong lớp `KtorPomodoroMiniClient` bằng cách sử dụng Ktor
   `HttpClient`.
4. **Ví dụ:**
    ```kotlin
    override suspend fun getNewData(): NewDataResponse {
        return client.get("$baseUrl/api/new-data").body()
    }
    ```

Để thêm các tài nguyên vào dự án:

1. **Vị trí:** Thêm tài nguyên vào thư mục `shared/src/commonMain/composeResources/`.
    * `drawable/`: Hình ảnh (png, jpg, svg, xml).
    * `values/`: Chuỗi văn bản (strings.xml).
    * `font/`: Các tệp phông chữ.
2. **Sử dụng:** Sử dụng đối tượng `Res` được tự động tạo ra.
3. **Ví dụ:**
    ```kotlin
    // Sử dụng hình ảnh
    painterResource(Res.drawable.your_image_name)

    // Sử dụng chuỗi văn bản
    stringResource(Res.string.your_string_key)
    ```

---

## 6. Best Practices

* **Nguyên tắc "Common First":** Luôn ưu tiên viết code vào `commonMain`.
* **Kiến trúc:** Khuyến khích sử dụng mô hình MVVM hoặc MVI. Tách biệt logic trong `features/` và
  UI.
* **Tài nguyên:** Sử dụng hệ thống quản lý tài nguyên của Compose Multiplatform.

---

Tìm hiểu thêm
tại: [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html), [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/).
