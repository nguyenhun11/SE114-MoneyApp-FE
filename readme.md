# 💰 MoneyApp - Quản lý tài chính cá nhân

MoneyApp là ứng dụng giúp người dùng theo dõi thu nhập, chi tiêu và quản lý tài chính một cách hiệu quả. Dự án được xây dựng theo kiến trúc **MVVM (Model-View-ViewModel)** chuẩn mực trên Android Native (Java).

---

### 🏛️ THÔNG TIN CHUNG
* **Trường**: Đại học Công nghệ Thông tin - ĐHQG TP.HCM (UIT)
* **Khoa**: Kỹ thuật Phần mềm
* **Môn học**: Nhập môn ứng dụng di động - SE114
* **Đề tài**: Quản lý tài chính cá nhân (MoneyApp)
* **Học kỳ**: Học kỳ 2 — Năm học 2025 - 2026

---

### 👥 DANH SÁCH THÀNH VIÊN NHÓM

| STT | Họ và tên | MSSV | Vai trò |
| :--- | :--- | :--- | :--- |
| 1 | Nguyễn Gia Hưng | 24520604 | Nhóm trưởng |
| 2 | Phạm Hoàng Sơn | 24521536 | Thành viên |
| 3 | Trần Lê Khánh Hưng | 24520629 | Thành viên |
| 4 | Ninh Đức Quang Huy | 24520688 | Thành viên |

---

### 🌟 GIỚI THIỆU ỨNG DỤNG (PROJECT OVERVIEW)
**MoneyApp** là ứng dụng di động quản lý chi tiêu, hỗ trợ giải quyết các khó khăn trong việc quản lý tài chính cá nhân. 

Hệ thống thiết kế theo mô hình Tài chính kết hợp Trò chơi hóa (Gamification). Tích hợp chức năng quản lý thu chi cơ bản với các tính năng tương tác: xây dựng thành phố, thực hiện nhiệm vụ và thu thập huy hiệu.

#### 💡 Điểm nổi bật kỹ thuật của dự án:
1. **Xử lý đa tiền tệ**: Lưu trữ đồng thời 3 thông số: số tiền gốc (OriginalAmount), số tiền quy đổi theo ví (AccountAmount) và tỷ giá tại thời điểm phát sinh (ExchangeRate).
2. **Tùy biến sắp xếp**: Sử dụng trường SortingOrder để lưu thứ tự hiển thị do người dùng cấu hình cho ví và danh mục.
3. **Nhất quán dữ liệu**: Áp dụng định dạng GUID làm khóa chính cho các bảng tài chính cốt lõi để đảm bảo tính duy nhất và tối ưu hóa đồng bộ dữ liệu.
4. **Cơ chế xóa mềm (Soft Delete)**: Sử dụng trường IsActive trên hầu hết các bảng để ẩn dữ liệu khi xóa, bảo toàn lịch sử hệ thống và toàn vẹn mối quan hệ dữ liệu.

---

### ✨ CÁC TÍNH NĂNG CHÍNH ĐÃ THỰC HIỆN

#### Ứng dụng phân thành các nhóm chức năng chính:

* **Users**: Bảng trung tâm. Lưu trữ thông tin định danh (tên, email, mật khẩu hash), thông tin cá nhân (ảnh, số điện thoại) và thông số hoạt động (DailyStreak, tiền tệ mặc định).
* **Accounts**: Quản lý danh sách ví/tài khoản (Tiền mặt, Thẻ ngân hàng, Tiết kiệm). Lưu trữ số dư (Balance), loại tiền tệ và cấu hình hiển thị (màu sắc, biểu tượng).
* **Categories & CategoryGroups**: Cấu trúc phân loại danh mục 2 cấp. CategoryGroup (Nhóm cha) chứa nhiều Category (Nhóm con). Hỗ trợ thiết lập mục tiêu chi tiêu hàng tháng cho từng danh mục.
* **Transactions**: Ghi vết chi tiết giao dịch thu/chi. Liên kết dữ liệu giữa Account và Category. Lưu trữ mở rộng: MoodId (tâm trạng) và ImageUrls (ảnh hóa đơn).
* **Transfers**: Ghi lại lịch sử chuyển tiền nội bộ giữa các ví. Tích hợp xử lý tỷ giá khi chuyển đổi giữa các loại tiền tệ khác nhau.
* **Calculator**: Tích hợp máy tính cầm tay để tính khi nhập tiền.
* **AdjustBalances**: Lưu vết các lệnh điều chỉnh số dư tài khoản thủ công (không tạo giao dịch thu/chi).
* **Goals**: Quản lý tiến độ tiết kiệm mục tiêu. Lưu trữ số tiền cần đạt, số tiền hiện có và hạn chót (Deadline).
* **Budgets**: Thiết lập giới hạn chi tiêu theo chu kỳ (Tuần/Tháng/Năm). Áp dụng cho toàn bộ hệ thống hoặc giới hạn theo từng danh mục cụ thể.
* **CityStates & Buildings**: Dữ liệu mô phỏng xây dựng thành phố. Người dùng tích lũy điểm thịnh vượng (ProsperityPoints) và ổn định (StabilityPoints) từ hành vi quản lý tài chính để mua và nâng cấp các công trình (Buildings) trên bản đồ tọa độ (X, Y).
* **Quests**: Quản lý các thử thách hành vi. Trạng thái thực hiện của người dùng được lưu trữ tại bảng UserQuests.
* **Badges**: Hệ thống huy hiệu đạt được khi hoàn thành cột mốc. Lịch sử cấp phát lưu tại bảng UserBadges.

---

### 🔑 TÀI KHOẢN TRẢI NGHIỆM SẴN CÓ (TEST CREDENTIALS)

> [!IMPORTANT]
> Để thuận tiện cho giảng viên chấm điểm, nhóm đã chuẩn bị sẵn các tài khoản để demo ứng dụng. Giảng viên chỉ cần đăng nhập bằng các tài khoản bên dưới để kiểm thử tất cả chức năng mà không cần tự tạo dữ liệu từ đầu:

| Tên đăng nhập / Email | Mật khẩu |
| :--- | :--- |
| `a@g` | `123456` |


---

### 🛠️ CÔNG NGHỆ & THƯ VIỆN SỬ DỤNG

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=materialdesign&logoColor=white)![Jetpack Navigation](https://img.shields.io/badge/Jetpack%20Navigation-3DDC84?style=for-the-badge&logo=android&logoColor=white)![Gson](https://img.shields.io/badge/Gson-4285F4?style=for-the-badge&logo=google&logoColor=white)![Room](https://img.shields.io/badge/Room-3DDC84?style=for-the-badge&logo=android&logoColor=white)![MPAndroidChart](https://img.shields.io/badge/MPAndroidChart-E91E63?style=for-the-badge&logo=chartdotjs&logoColor=white)![Retrofit](https://img.shields.io/badge/Retrofit%202-48B983?style=for-the-badge)![OkHttp](https://img.shields.io/badge/OkHttp-3EAAAF?style=for-the-badge)![Glide](https://img.shields.io/badge/Glide-4CAF50?style=for-the-badge)![Firebase Auth](https://img.shields.io/badge/Firebase%20Auth-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)![Google Sign-In](https://img.shields.io/badge/Google%20Sign--In-4285F4?style=for-the-badge&logo=google&logoColor=white)![Mikepenz Iconics](https://img.shields.io/badge/Mikepenz%20Iconics-212121?style=for-the-badge)

* **Java**: Ngôn ngữ lập trình chính cho ứng dụng.
* **Google Material Components**: Cung cấp các UI Widget chuẩn hóa theo phong cách thiết kế Material Design hiện đại giúp giao diện nhất quán và chuyên nghiệp.
* **Jetpack Navigation Component**: Quản lý luồng di chuyển giữa các màn hình tập trung qua đồ thị điều hướng trực quan, xử lý Back Stack tự động và truyền tham số an toàn.
* **Gson (Google)**: Chuyển đổi qua lại giữa chuỗi định dạng JSON nhận từ Server và đối tượng Java POJO nhanh chóng, giảm thiểu lỗi parse dữ liệu bằng tay.
* **Room Persistence Library**: Lưu trữ dữ liệu cục bộ.
* **MPAndroidChart**: Thư viện biểu đồ chuyên sâu.
* **Retrofit 2 & OkHttp**: Hỗ trợ kết nối và gửi yêu cầu mạng.
* **Glide**: Tải và lưu bộ nhớ đệm (caching) hình ảnh hiệu quả, tối ưu hóa việc hiển thị các icon hoặc ảnh đại diện của người dùng mà không gây đầy bộ nhớ RAM.
* **Firebase Auth & Google Sign-In**: Xác thực bảo mật.
* **Mikepenz Iconics**: Quản lí Icons.

---

### 💻 HƯỚNG DẪN CÀI ĐẶT & CHẠY ỨNG DỤNG

#### 1. Yêu cầu cấu hình hệ thống
* **Hệ điều hành**: Windows 10/11, macOS, hoặc Linux.
* **IDE**: Android Studio (Koala, Ladybug hoặc phiên bản mới hơn).
* **Java SDK**: JDK 11 hoặc mới hơn (Android Studio đã tích hợp sẵn).
* **Android SDK**: Compile SDK 36 (Android 16), Min SDK 24 (Android 7.0).
* **Thiết bị chạy thử**: Thiết bị ảo (Emulator) hoặc điện thoại thật hỗ trợ API Level 24 trở lên.

#### 2. Các bước mở và chạy dự án trong Android Studio

1. **Tải mã nguồn về máy**:
   * Giải nén file nén nguồn hoặc chạy lệnh:
     ```bash
     git clone https://github.com/nguyenhun11/SE114-MoneyApp-FE.git
     ```
2. **Mở dự án trên Android Studio**:
   * Mở Android Studio, click chọn **Open**.
   * Dẫn đường dẫn đến thư mục chứa mã nguồn dự án (thư mục chứa tệp `settings.gradle.kts`).
3. **Đồng bộ hóa Gradle**:
   * Hãy đợi khoảng 1-3 phút để Android Studio tải các dependencies và hoàn tất **Gradle Sync**.
4. **Chạy ứng dụng (Run)**:
   * Kết nối thiết bị Android thật (đã bật chế độ gỡ lỗi USB) hoặc khởi chạy thiết bị ảo (Emulator).
   * Bấm biểu tượng nút **Run (▶️)** trên thanh công cụ phía trên hoặc bấm tổ hợp phím `Shift + F10` (trên Windows/Linux).
   * Chờ quá trình build hoàn tất, file APK sẽ được cài đặt và ứng dụng tự khởi động trên thiết bị.

---
*Đồ án được thực hiện nhằm mục đích học tập môn Nhập môn ứng dụng di động tại UIT - Khoa Kỹ thuật phần mềm.*