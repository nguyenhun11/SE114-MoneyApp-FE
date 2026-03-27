# Quản lý chi tiêu

MoneyApp là ứng dụng quản lý tài chính cá nhân giúp người dùng theo dõi thu nhập và chi tiêu một
cách hiệu quả.

## Cấu trúc dự án

```text
app/src/main/java/com/example/moneyapp/
├── adapter/        # Các Adapter cho RecyclerView
├── api/            # Kết nối API và Retrofit
├── model/          # Các lớp dữ liệu
├── ui/             # Giao diện người dùng
│   ├── home/       # Fragment Trang chủ
│   ├── profile/    # Fragment Hồ sơ cá nhân
│   ├── statistics/ # Fragment Thống kê
│   ├── transaction/# Fragment Giao dịch
│   └── MainActivity.java
└── utils/          # Các lớp tiện ích (Helper classes)
app/src/main/res/
├── layout/         # Các tệp giao diện XML
├── menu/           # Định nghĩa menu (bottom_nav_menu.xml)
├── drawable/       # Các icon và hình ảnh
└── values/         # Colors, Strings, Styles
```

## Chức năng

- **Trang chủ (Home):** Tổng quan về số dư, thống kê thu nhập và chi tiêu trong 1 khoảng thời gian theo hạng mục, tài khoản.
- **Giao dịch (Transaction):** Ghi chép các khoản thu chi, lọc theo thời gian, tài khoản, hạng mục.
- **Tài khoản (Accounts)**: Các tài khoản ngân hàng, tiền mặt, tiền điện tử,... Có thể bao gồm hoặc không vào tổng số dư. Chuyển tiền qua lại giữa các tài khoản.
- **Hạng mục (Categories)**: Sửa đổi danh sách hạng mục thu, thi, icon, màu sắc.
- **Thống kê (Statistics):** Thống kê thu nhập, chi tiêu, số dư thành biểu đồ cột trong nhiều khoảng thời gian gần đây.
- **Hồ sơ (Profile):** Quản lý thông tin cá nhân và cài đặt ứng dụng.
  - Ngôn ngữ
  - Tài khoản mặc định khi thêm giao dịch.
  - Đơn vị thời gian mặc định, ngày bắt đầu tuần (thứ 2, chủ nhật)
  - Đơn vị tiền mặc định (VND, USD) và đơn vị số (000 VND)
  - Tài khoản hiển thị số dư mặc định (tổng cộng, tiền mặt,...)
- **Dữ liệu:** 
  - Đăng nhập, đăng ký
  - Xuất, nhập excel để backup dữ liệu
  - Xóa tài khoản

## Công nghệ sử dụng

- **Android SDK:** Java/Kotlin
- **UI Components:** Material Design, ConstraintLayout, BottomNavigationView
- **Navigation:** Jetpack Navigation Component
- **Networking:** Retrofit (dự kiến)

## Cấu trúc giao diện

- `activity_main.xml`: Giao diện chính chứa `FragmentContainerView` và `BottomNavigationView`.
- `fragment_home.xml`: Giao diện hiển thị thông tin tổng quan.
- `bottom_nav_menu.xml`: Menu điều hướng phía dưới.

## Cơ sở dữ liệu

(Đang cập nhật - Dự kiến sử dụng Room Database hoặc Firebase)

## Quy định làm việc
- Mỗi thành viên khi làm chức năng mới phải tạo nhánh riêng tên feat/ten-chuc-nang
- Trước khi push code, hãy pull về trước
- Nhánh làm việc chính là nhánh main
- Liên hệ Hung khi gặp lỗi
