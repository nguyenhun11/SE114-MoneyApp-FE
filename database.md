# Phần 1: Cấu trúc Cơ sở dữ liệu (Database Schema)
## 1. Quản lý người dùng (User)
Bảng lưu trữ thông tin tài khoản người dùng, phục vụ đăng nhập và định danh.

```SQL
table User {
  UserID varchar(36) [primary key]    
  Username varchar [unique]
  Email varchar [unique]
  UserFullName nvarchar
  PasswordHashed varchar
  CreatedAt datetime              
}
```
## 2. Các hạng mục (Category)
Bảng định nghĩa các nhóm Thu nhập và Chi tiêu (VD: Ăn uống, Lương, Mua sắm...).
```SQL
table Category {
  CategoryID varchar(36) [primary key]     
  UserID varchar(36)            --> ref: User.UserID
  TypeID int                    -- Phân loại: 0 (Hệ thống), 1 (Chi tiêu), 2 (Thu nhập)
  CategoryName nvarchar
  TargetAmount money            -- Hạn mức chi tiêu (Budget)             
  IconID int
  ColorID int
  -- Dữ liệu đồng bộ
  IsSynced boolean
  IsDeleted boolean             -- Soft delete (Xóa mềm)            
  UpdatedAt datetime             
}
```
## 3. Nguồn tiền (Account)
Bảng quản lý các ví chứa tiền (VD: Tiền mặt, Thẻ tín dụng, Momo...).

```SQL
table Account {
  AccountID varchar(36) [primary key]         
  UserID varchar(36)            --> ref: User.UserID
  AccountName nvarchar
  CurrentBalance money          -- Số dư hiện tại
  IncludeInTotal boolean        -- Có tính vào tổng tài sản không
  IconID int
  ColorID int
  -- Dữ liệu đồng bộ
  IsSynced boolean
  IsDeleted boolean             -- Soft delete
  UpdatedAt datetime
}
```

## 4. Giao dịch (Transaction)
Bảng trung tâm lưu trữ toàn bộ lịch sử biến động số dư.

```SQL
table Transaction {
  TransactionID varchar(36) [primary key]    
  UserID varchar(36)            --> ref: User.UserID
  TransactionType int           -- 1: Chi tiêu, 2: Thu nhập, 3: Chuyển khoản
  Amount money
  SourceAccountID varchar(36)   --> ref: Account.AccountID (Ví nguồn)
  DestAccountID varchar(36) [null] --> ref: Account.AccountID (Ví đích - Chỉ dùng khi Type=3)
  CategoryID varchar(36) [null] --> ref: Category.CategoryID (Null nếu là chuyển khoản)   
  Date datetime                  
  Description nvarchar [null]
  -- Dữ liệu đồng bộ
  IsSynced boolean
  IsDeleted boolean             -- Soft delete
  UpdatedAt datetime
}
```
# Phần 2: Logic xử lý các đối tượng chức năng
⚠️ Nguyên tắc chung: Toàn bộ các tác vụ có làm thay đổi CurrentBalance (Số dư) bắt buộc phải được đặt trong một Database Transaction (Dùng @Transaction trong Room) để đảm bảo tính toàn vẹn dữ liệu. Nếu 1 bước lỗi, toàn bộ thao tác phải được Roll-back.

## 2.1. Xử lý thay đổi dữ liệu Giao dịch
### A. Thêm Chi tiêu (TransactionType = 1)
1. Insert Transaction: Tạo mới 1 bản ghi với Amount, SourceAccountID, CategoryID, Date. DestAccountID để null.

2. Update Account: Trừ tiền ở ví nguồn.
```sql
UPDATE Account SET CurrentBalance = CurrentBalance - Amount, UpdatedAt = NOW(), IsSynced = false WHERE AccountID = SourceAccountID
```

### B. Thêm Thu nhập (TransactionType = 2)
1. Insert Transaction: Tạo mới 1 bản ghi với Amount, SourceAccountID (Ví nhận tiền), CategoryID, Date.

2. Update Account: Cộng tiền vào ví đích.
```sql
UPDATE Account SET CurrentBalance = CurrentBalance + Amount, UpdatedAt = NOW(), IsSynced = false WHERE AccountID = SourceAccountID
```

### C. Chuyển tiền giữa các Nguồn tiền (TransactionType = 3)
1. Insert Transaction: Tạo mới bản ghi có đầy đủ SourceAccountID (Ví chuyển) và DestAccountID (Ví nhận). CategoryID để null.

2. Update Source Account: Trừ tiền ở ví chuyển.
```sql
CurrentBalance = CurrentBalance - Amount
```
3. Update Dest Account: Cộng tiền ở ví nhận.
```sql
CurrentBalance = CurrentBalance + Amount
```

## 2.2. Xử lý Đối tượng danh mục (Master Data)
### A. Thêm Hạng mục / Nguồn tiền
Action: Sinh UUID mới và thực hiện lệnh INSERT vào bảng tương ứng. Các trường IsSynced = false, IsDeleted = false.

### B. Xóa Hạng mục / Nguồn tiền (Soft Delete)

Action: Dùng lệnh UPDATE để ẩn dữ liệu khỏi giao diện.
```sql
UPDATE Category SET IsDeleted = true, IsSynced = false, UpdatedAt = NOW() WHERE CategoryID = ...
```
Khi người dùng bấm xóa một Hạng mục đang có giao dịch liên quan, hiển thị Popup yêu cầu chọn 1 trong 2 hướng xử lý:

- Lựa chọn 1: Chuyển toàn bộ giao dịch sang Hạng mục khác

    - Bước 1: Update toàn bộ giao dịch cũ trỏ sang ID của hạng mục mới.
        ```sql
        UPDATE Transaction SET CategoryID = :newCategoryID, UpdatedAt = NOW(), IsSynced = false WHERE CategoryID = :oldCategoryID AND IsDeleted = false
        ```
    - Bước 2: Xóa mềm (Soft-delete) Hạng mục cũ.

- Lựa chọn 2: Xóa toàn bộ giao dịch liên quan.
Lưu ý: Vì xóa giao dịch sẽ ảnh hưởng đến số dư, phải bọc trong @Transaction.
   - Bước 1: Hoàn tiền lại cho Nguồn tiền (Cộng lại nếu là Chi, Trừ đi nếu là Thu). Bước này có thể dùng vòng lặp trong code hoặc viết câu SQL phức tạp để tính toán tổng tiền cần hoàn.
   - Bước 2: Xóa mềm toàn bộ giao dịch thuộc Hạng mục đó.
    - Bước 3: Xóa mềm Hạng mục cũ.

Lưu ý UI: Khi query danh sách hiển thị lên màn hình, luôn luôn thêm điều kiện WHERE IsDeleted = false.

## 2.3. Kết xuất thống kê và Báo cáo
### A. Thống kê theo Hạng mục (Biểu đồ tròn)
Logic: Gom nhóm các giao dịch theo từng Hạng mục trong một khoảng thời gian nhất định để tính tổng.

Truy vấn mẫu:

```SQL
SELECT c.CategoryName, c.ColorID, SUM(t.Amount) as TotalAmount
FROM Transaction t
JOIN Category c ON t.CategoryID = c.CategoryID
WHERE t.TransactionType = 1              -- Lọc loại (Chi hoặc Thu)
  AND t.IsDeleted = false 
  AND t.Date BETWEEN :startDate AND :endDate
GROUP BY c.CategoryID
```
### B. Lịch sử Giao dịch
Logic: Truy vấn danh sách và có thể filter động theo Tài khoản, Thu/Chi, Thời gian.

Truy vấn mẫu:

```SQL
SELECT * FROM Transaction 
WHERE IsDeleted = false 
  AND TransactionType IN (1, 2)          -- Ẩn các lệnh chuyển khoản nội bộ
  AND Date BETWEEN :startDate AND :endDate
ORDER BY Date DESC
```