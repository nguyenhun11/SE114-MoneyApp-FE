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

---

### ✨ CÁC TÍNH NĂNG CHÍNH ĐÃ THỰC HIỆN

#### Ứng dụng phân thành các nhóm chức năng chính:

* **Users & Authentication:** Bảng định danh trung tâm quản lý thông tin cá nhân (ảnh, email), thông số Gamification (DailyStreak) và tích hợp hệ thống xác thực bảo mật đa luồng (Google Sign-In, OTP quên mật khẩu, Token Rotation ngầm).
* **Accounts:** Quản lý danh sách đa dạng các ví/tài khoản (Tiền mặt, Thẻ ngân hàng, Tiết kiệm) bao gồm số dư nội tại, loại tiền tệ và cấu hình hiển thị UI (màu sắc, biểu tượng).
* **Categories & CategoryGroups:** Cấu trúc phân loại danh mục thu/chi 2 cấp (Nhóm cha - Nhóm con), hỗ trợ phân luồng dòng tiền chi tiết và làm cơ sở để thiết lập mục tiêu chi tiêu.
* **Transactions:** Ghi vết chi tiết mọi giao dịch thu/chi, liên kết chéo dữ liệu giữa Account và Category, đồng thời lưu trữ mở rộng cảm xúc (MoodId) và hình ảnh hóa đơn (ImageUrls).
* **Pending Transactions:** Quản lý cơ sở dữ liệu nháp cục bộ (Room DB) lưu trữ các giao dịch chờ duyệt, đi kèm hệ thống đếm và hiển thị cảnh báo (Banner) trực tiếp tại màn hình chính.
* **Notifications:** Theo dõi thông báo từ điện thoại (với sự cho phép của người dùng) để lắng nghe các thông báo biến động số dư từ tin nhắn SMS hoặc thông báo ngân hàng, tạo các giao dịch chờ duyệt.
* **Transfers:** Ghi lại lịch sử luân chuyển dòng tiền nội bộ giữa các ví, tự động đồng bộ và trừ/cộng số dư của tài khoản nguồn và tài khoản đích.
* **Currency Exchange:** Xử lý và quy đổi linh hoạt tỷ giá giữa các loại tiền tệ khác nhau, hỗ trợ tính toán chính xác giá trị khi thực hiện giao dịch ngoại tệ hoặc chuyển tiền khác loại ví.
* **Calculator:** Tích hợp bộ máy tính cầm tay mini ngay trên bàn phím nhập liệu, cho phép người dùng tính toán nhanh các biểu thức trực tiếp vào ô số tiền.
* **AdjustBalances:** Lưu vết các lệnh điều chỉnh số dư tài khoản thủ công nhằm xử lý chênh lệch thực tế mà không sinh ra giao dịch thu/chi làm nhiễu báo cáo.
* **Reports & Charts:** Hệ thống phân tích tài chính sử dụng thư viện MPAndroidChart kết xuất các báo cáo xu hướng thu/chi dưới dạng biểu đồ trực quan (PieChart, BarChart).
* **Goals:** Quản lý tiến độ các mục tiêu tiết kiệm, liên tục tính toán số tiền hiện có so với số tiền cần đạt và theo dõi sát sao hạn chót (Deadline).
* **Budgets:** Thiết lập và giám sát giới hạn chi tiêu theo các chu kỳ (Tuần/Tháng/Năm), có thể áp dụng khống chế cho tổng ngân sách hoặc siết chặt theo từng danh mục riêng biệt.
* **CityStates & Buildings:** Hệ thống trò chơi hóa (Gamification) mô phỏng thành phố, nơi người dùng dùng điểm Thịnh vượng/Ổn định kiếm được từ việc quản lý tài chính kỷ luật để mua và nâng cấp công trình trên tọa độ (X, Y).
* **Quests:** Quản lý hệ thống thử thách và nhiệm vụ tài chính định kỳ nhằm tạo động lực ghi chép, tiến trình hoàn thành được lưu vết chi tiết tại bảng UserQuests.
* **Badges:** Hệ thống vinh danh cấp phát huy hiệu (thành tựu) khi người dùng đạt được các cột mốc quản lý tài chính quan trọng, lịch sử nhận huy hiệu lưu tại UserBadges.

---

### 🔑 TÀI KHOẢN TRẢI NGHIỆM SẴN CÓ

> [!IMPORTANT]
> Để thuận tiện cho giảng viên chấm điểm, nhóm đã chuẩn bị sẵn các tài khoản để demo ứng dụng. Giảng viên chỉ cần đăng nhập bằng các tài khoản bên dưới để kiểm thử tất cả chức năng mà không cần tự tạo dữ liệu từ đầu:

| Email | Mật khẩu |
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


1. **Tải mã nguồn về máy tính**:
* Tiến hành giải nén tệp mã nguồn đính kèm hoặc sao chép dự án từ kho lưu trữ GitHub bằng dòng lệnh sau:
   ```bash
   git clone https://github.com/nguyenhun11/SE114-MoneyApp-FE.git

   ```


2. **Cấu hình dịch vụ Firebase/Google Auth**:
* Ứng dụng yêu cầu tệp định tuyến bảo mật `google-services.json` để kết nối với các dịch vụ xác thực.
* **Dành cho Giảng viên đánh giá:** Nhóm đã chuẩn bị sẵn tệp cấu hình hoàn chỉnh trên Google Drive (đính kèm cùng link nộp đồ án). Thầy chỉ cần tải về trực tiếp vào thư mục `app/` của dự án.
* **Dành cho thành viên phát triển:** Chạy lệnh copy dưới đây tại thư mục gốc để sinh ra tệp cấu hình từ tệp mẫu, sau đó liên hệ Tech Lead để lấy các biến môi trường thay thế vào:
   ```bash
   cp app/google-services.sample.json app/google-services.json

   ```

3. **Mở dự án trên nền tảng Android Studio**:
* Khởi động phần mềm **Android Studio**, tại màn hình chào mừng chọn chức năng **Open** (hoặc File > Open).
* Điều hướng đến thư mục mã nguồn vừa tải về (chọn đúng thư mục gốc có chứa tệp `settings.gradle.kts`) và nhấn **OK**.


4. **Đồng bộ hóa môi trường (Gradle Sync)**:
* Hệ thống sẽ tự động tiến hành tải các thư viện (dependencies) cần thiết để chạy dự án.
* Vui lòng duy trì kết nối mạng và đợi khoảng 1-3 phút để quá trình **Gradle Sync** hoàn tất không báo lỗi ở cửa sổ Build.


5. **Biên dịch và chạy ứng dụng (Run)**:
* Kết nối thiết bị di động Android vật lý (yêu cầu đã bật chế độ *Gỡ lỗi USB - USB Debugging*) hoặc khởi chạy Máy ảo Android (Emulator) tích hợp sẵn.
* Nhấn biểu tượng **Run (▶️)** màu xanh trên thanh công cụ phía trên, hoặc sử dụng tổ hợp phím `Shift + F10` (trên Windows/Linux).
* Chờ quá trình biên dịch (Build) hoàn tất, tệp APK sẽ được triển khai tự động và ứng dụng MoneyApp sẽ khởi chạy trên thiết bị.

---
*Đồ án được thực hiện nhằm mục đích học tập môn Nhập môn ứng dụng di động tại UIT - Khoa Kỹ thuật phần mềm.*
