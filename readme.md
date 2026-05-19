# 💰 MoneyApp - Quản lý tài chính cá nhân

MoneyApp là ứng dụng giúp người dùng theo dõi thu nhập, chi tiêu và quản lý tài chính một cách hiệu quả. Dự án được xây dựng theo kiến trúc **MVVM (Model-View-ViewModel)** chuẩn mực trên Android Native (Java).

---

## 🏗 Kiến trúc dự án (Architecture)

Dự án chia làm 3 tầng chính để đảm bảo tính dễ bảo trì và mở rộng:

### 1. Tầng Dữ liệu (Data Layer - `data/`)
Là "Single Source of Truth" của ứng dụng, quản lý mọi nguồn dữ liệu.
- **`local/`**: Quản lý Database SQLite (Room).
    - `entity/`: Các **Entity** (đại diện cho bảng trong DB).
    - `dao/`: Các interface định nghĩa câu lệnh truy vấn.
- **`remote/`**: Quản lý kết nối mạng (Retrofit).
    - `api/`: Định nghĩa các API Endpoints.
    - `request/` & `response/`: Các đối tượng trao đổi dữ liệu với Server.
- **`repository/`**: Tầng điều phối dữ liệu. Quyết định lấy dữ liệu từ API hay Local DB và xử lý logic gộp dữ liệu.
### 2. Tầng ViewModel (`viewmodel/` hoặc theo `ui/feature/`)
- Xử lý logic nghiệp vụ (Business Logic).
- Giữ trạng thái của UI (UI State)
- **Quy tắc**: Cung cấp phương thức cho View, nhận dữ liệu từ Data, không để View gọi trực tiếp Data

### 3. Tầng Giao diện (View Layer - `ui/`)
- Chỉ làm nhiệm vụ hiển thị, điều hướng dữ liệu và gửi sự kiện từ người dùng đến ViewModel.
- **`BaseFragment.java`**: Fragment cha cung cấp các tiện ích dùng chung (Header, Tabs, FAB).
- **`MainActivity.java`**: Single Activity điều phối Navigation.
- **`MainUIHandler.java`**: Xử lý logic hiển thị các thành phần UI hệ thống (BottomBar, FAB).

---

## 📂 Cấu trúc thư mục chi tiết

```text
app/src/main/java/com/example/moneyapp/
├── data/
│   ├── local/          # Database (Room)
│   │   ├── dao/        # Data Access Objects
│   │   └── entity/      # Entities (Bảng database)
│   ├── remote/         # Network (Retrofit)
│   │   ├── api/        # API Interfaces
│   │   ├── request/    # Đối tượng gửi lên API
│   │   └── response/    # Đối tượng nhận về từ API
│   └── repository/     # Repositories (Điều phối dữ liệu)
│   
├── ui/                 # View Layer (Phân chia theo tính năng)
│   ├── home/           # HomeFragment & HomeViewModel
│   ├── auth/           # Login, Register, Forgot Password
│   ├── transaction/    # Quản lý giao dịch
│   ├── .../            # Các thành phần khác
│   ├── BaseFragment.java
│   └── MainActivity.java
├── util/               # Tiện ích (Format tiền, ngày tháng, v.v.)
└── viewmodel/          # Chứa ViewModel
```

---

## 🔄 Quy trình truy cập dữ liệu (Data Flow)

Tuân thủ luồng dữ liệu **một chiều**:
1. **User Interaction**: Người dùng thao tác trên Fragment.
2. **View -> ViewModel**: Fragment gọi hàm xử lý trong ViewModel.
3. **ViewModel -> Repository**: ViewModel yêu cầu dữ liệu từ Repository.
4. **Repository -> Remote/Local**: Repository lấy dữ liệu từ API hoặc Database.
5. **Data -> ViewModel**: Repository trả dữ liệu về (thường qua Callback hoặc trực tiếp).
6. **ViewModel -> View**: ViewModel cập nhật kết quả vào `LiveData`.
7. **UI Update**: Fragment `observe` LiveData và tự động cập nhật giao diện.

---

## 🛠 Hướng dẫn

### 1. Cách tạo màn hình mới (ví dụ: Quản lý Ví)
- **Data**: Tạo `WalletEntity` trong `local/model` và `WalletDao`.
- **Repository**: Tạo `WalletRepository` để xử lý logic lấy/lưu ví.
- **ViewModel**: Tạo `WalletViewModel`, gọi Repository và cung cấp `LiveData<List<Wallet>>`.
- **View**: Tạo `WalletFragment` kế thừa `BaseFragment`, sử dụng `setupHeader` và `observe` dữ liệu.

### 2. Sử dụng BaseFragment
Kế thừa `BaseFragment` để tận dụng các hàm có sẵn:
- `setupHeader(view, title, showBackBtn)`: Cài đặt tiêu đề trang.
- `setupIncomeExpenseTabs(view, listener)`: Cài đặt tab Thu/Chi.
- Override `getFabIcon()` và `onFabClick()` để điều khiển nút Floating Action Button.

### 3. Quy định làm việc
- **KHÔNG** gọi trực tiếp Database/API trong Fragment.
- **KHÔNG** truyền Context vào ViewModel.
- Nhánh Git: `feat/ten-chuc-nang`.
- Sử dụng các hàm trong `util/` để định dạng tiền tệ và thời gian.
