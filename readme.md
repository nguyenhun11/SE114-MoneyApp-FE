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
│   ├── MainActivity.java    # [1] Activity chính điều phối Navigation
│   ├── MainUIHandler.java   # [2] Xử lý UI tập trung (FAB, BottomNav, Popup)
│   ├── BaseFragment.java    # [3] Fragment cơ sở cho mọi màn hình
│   ├── SplashActivity.java  # [4] Màn hình chào và kiểm tra đăng nhập
│   ├── home/                # Trang chủ
│   ├── account/             # Tài khoản
│   ├── transaction/         # Giao dịch
│   └── ...                  # Các phân hệ UI khác (auth, profile, statistics...)
└── utils/          # Các lớp tiện ích (CurrencyFormatter, DateTimeUtils...)
```

## Kiến trúc UI chính (4 File quan trọng trong package `ui`)

Dự án tuân thủ mô hình **Single Activity**, trong đó 4 file sau đây đóng vai trò nền tảng:

1. **`MainActivity.java`**: 
   - Đóng vai trò là "Host" (vật chủ) duy nhất chứa `NavHostFragment`.
   - Cung cấp các phương thức public như `setBottomNavigationVisibility()` và `updateFAB()` để các Fragment con có thể điều khiển giao diện chung.

2. **`MainUIHandler.java`**:
   - Tách biệt logic xử lý giao diện khỏi Activity. 
   - Tự động lắng nghe sự thay đổi màn hình (`DestinationChangedListener`) để ẩn/hiện BottomNav hoặc đổi trạng thái Menu.
   - Quản lý các Popup dùng chung (như menu "Thêm" hoặc "Thêm nữa").

3. **`BaseFragment.java`**:
   - Mọi Fragment trong dự án (Home, Transaction, Detail...) **bắt buộc** kế thừa từ đây.
   - Giúp đồng bộ hóa UI: Fragment con chỉ cần ghi đè (override) `getFabIcon()` hoặc `shouldShowBottomNavigation()` để thay đổi trạng thái giao diện mà không cần viết code xử lý phức tạp.

4. **`SplashActivity.java`**:
   - Điểm đầu vào của ứng dụng. Xử lý logic khởi tạo, kiểm tra token đăng nhập và chuyển hướng người dùng vào màn hình chính hoặc màn hình đăng nhập.

## Cách sử dụng BaseFragment cho thành viên mới

Để tạo một màn hình mới, hãy kế thừa `BaseFragment`:

```java
public class MyNewFragment extends BaseFragment {
    
    @Override
    protected int getFabIcon() {
        return R.drawable.ic_plus; // Đổi icon FAB
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false; // Ẩn thanh điều hướng dưới nếu cần
    }

    @Override
    protected void onFabClick() {
        // Xử lý sự kiện khi nhấn nút FAB
    }
}
```

## Quy định làm việc
- Mỗi thành viên khi làm chức năng mới phải tạo nhánh riêng tên `feat/ten-chuc-nang`.
- Luôn kế thừa `BaseFragment` để đảm bảo logic FAB và BottomNav hoạt động đúng.
- Sử dụng `layout_header_common` cho phần đầu trang của các Fragment để đồng nhất UI.
- Các hàm tiện ích dùng chung (định dạng tiền, ngày tháng) hãy đặt trong package `utils`.
