package com.example.moneyapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * Thực thể đại diện cho bảng pending_transactions trong cơ sở dữ liệu local (Room).
 * Bảng này dùng để lưu tạm thời các giao dịch tự động nhận diện được từ thông báo (SMS, Vietcombank, Momo, Techcombank, MB Bank, v.v.).
 * Các giao dịch này sẽ ở dạng "bản nháp" và chờ người dùng duyệt (Approve) hoặc xóa bỏ (Ignore/Delete).
 * Khi được duyệt, bản nháp sẽ được chuyển đổi thành giao dịch chính thức trong bảng transactions và xóa khỏi bảng này.
 */
@Entity(tableName = "pending_transactions")
public class PendingTransaction {

    // Khóa chính của bảng giao dịch chờ duyệt, sử dụng UUID được tạo ngẫu nhiên dưới dạng chuỗi String
    @PrimaryKey
    @NonNull
    private String id;

    // Loại giao dịch: 1 đại diện cho Expense (Chi tiêu), 2 đại diện cho Income (Thu nhập), theo cấu trúc thực thể Transaction
    private int transactionType;

    // Số tiền giao dịch phát hiện được từ thông báo (kiểu dữ liệu số thực double)
    private double amount;

    // Ghi chú/Nội dung chuyển tiền được bóc tách từ tin nhắn biến động số dư
    private String note;

    // Tên tài khoản hoặc nguồn ngân hàng được nhận diện (Ví dụ: "Momo", "Vietcombank", "Techcombank", "MB Bank")
    private String accountName;

    // Zeit chốt giao dịch được ghi nhận trên hệ thống (ngày giờ tạo bản ghi nháp)
    private Date createdAt;

    /**
     * Hàm khởi tạo đầy đủ tham số để Room Database map dữ liệu khi truy vấn.
     * 
     * @param id khóa chính UUID
     * @param transactionType loại giao dịch (1: Chi, 2: Thu)
     * @param amount số tiền giao dịch
     * @param note ghi chú / nội dung giao dịch
     * @param accountName tên tài khoản nguồn được nhận diện
     * @param createdAt ngày tạo bản ghi nháp
     */
    public PendingTransaction(@NonNull String id, int transactionType, double amount, String note, String accountName, Date createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.note = note;
        this.accountName = accountName;
        this.createdAt = createdAt;
    }

    /**
     * Hàm khởi tạo tiện ích dùng khi tạo mới một giao dịch chờ duyệt từ Service lắng nghe thông báo.
     * Hàm này sẽ tự động sinh UUID cho khóa chính và gán thời gian hiện tại cho trường createdAt.
     * 
     * @param transactionType loại giao dịch (1: Chi, 2: Thu)
     * @param amount số tiền giao dịch
     * @param note ghi chú / nội dung chuyển khoản
     * @param accountName tên tài khoản nguồn được nhận diện
     */
    @Ignore
    public PendingTransaction(int transactionType, double amount, String note, String accountName) {
        this.id = UUID.randomUUID().toString(); // Tự động sinh ID ngẫu nhiên không trùng lặp
        this.transactionType = transactionType;
        this.amount = amount;
        this.note = note;
        this.accountName = accountName;
        this.createdAt = new Date(); // Thiết lập ngày tạo là thời gian hiện tại
    }

    // --- Các phương thức Getter và Setter với comment chi tiết ---

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
