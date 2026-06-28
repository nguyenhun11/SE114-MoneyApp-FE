package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.moneyapp.data.local.entity.PendingTransaction;

import java.util.List;

/**
 * Interface Data Access Object (DAO) định nghĩa các phương thức tương tác cơ sở dữ liệu
 * cho bảng pending_transactions sử dụng thư viện Room Persistence.
 */
@Dao
public interface PendingTransactionDao {

    /**
     * Thêm mới một giao dịch chờ duyệt vào cơ sở dữ liệu.
     * Nếu xảy ra xung đột khóa chính (trùng ID), thực hiện ghi đè (Replace).
     * 
     * @param pendingTransaction đối tượng giao dịch chờ thêm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPendingTransaction(PendingTransaction pendingTransaction);

    /**
     * Lấy toàn bộ danh sách các giao dịch chờ duyệt, sắp xếp theo thời gian tạo mới nhất lên trên đầu.
     * 
     * @return danh sách các đối tượng PendingTransaction
     */
    @Query("SELECT * FROM pending_transactions ORDER BY createdAt DESC")
    List<PendingTransaction> getAllPendingTransactions();

    /**
     * Lấy một giao dịch chờ duyệt theo ID cụ thể.
     * 
     * @param id khóa chính của giao dịch
     * @return đối tượng PendingTransaction hoặc null nếu không tìm thấy
     */
    @Query("SELECT * FROM pending_transactions WHERE id = :id LIMIT 1")
    PendingTransaction getPendingTransactionById(String id);

    /**
     * Đếm tổng số lượng giao dịch chờ duyệt hiện có trong cơ sở dữ liệu.
     * Dùng để kiểm tra và hiển thị số lượng trên Banner ở HomeFragment.
     * 
     * @return số lượng giao dịch chờ duyệt
     */
    @Query("SELECT COUNT(*) FROM pending_transactions")
    int getPendingCount();

    /**
     * Xóa một đối tượng giao dịch chờ duyệt ra khỏi cơ sở dữ liệu.
     * Thường dùng sau khi người dùng bấm nút xóa bỏ/từ chối giao dịch nháp đó.
     * 
     * @param pendingTransaction đối tượng cần xóa
     */
    @Delete
    void deletePendingTransaction(PendingTransaction pendingTransaction);

    /**
     * Xóa giao dịch chờ duyệt theo ID cụ thể.
     * Thường dùng sau khi người dùng bấm "Duyệt" (lưu chính thức) hoặc xóa nhanh bằng ID.
     * 
     * @param id khóa chính của giao dịch chờ duyệt cần xóa
     */
    @Query("DELETE FROM pending_transactions WHERE id = :id")
    void deletePendingTransactionById(String id);

    /**
     * Xóa sạch toàn bộ giao dịch chờ duyệt hiện có trong bảng.
     * Hỗ trợ tính năng dọn dẹp hoặc xóa nhanh tất cả nháp.
     */
    @Query("DELETE FROM pending_transactions")
    void deleteAllPendingTransactions();
}
