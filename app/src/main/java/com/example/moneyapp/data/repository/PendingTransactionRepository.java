package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.PendingTransactionDao;
import com.example.moneyapp.data.local.entity.PendingTransaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lớp Repository quản lý việc tương tác với dữ liệu giao dịch chờ duyệt (PendingTransaction).
 * Đóng vai trò là trung gian điều phối giữa các thành phần khác (Service, UI) và Database.
 * Toàn bộ các thao tác ghi đọc được thực thi bất đồng bộ trên một luồng nền để tránh gây đơ UI.
 */
public class PendingTransactionRepository {

    private final PendingTransactionDao pendingTransactionDao;
    private final ExecutorService executorService;
    private final Context context;

    /**
     * Interface callback dùng để phản hồi kết quả truy vấn danh sách hoặc đối tượng giao dịch chờ duyệt.
     */
    public interface PendingTransactionCallback {
        void onSuccess(List<PendingTransaction> pendingTransactions);
        void onSuccess(PendingTransaction pendingTransaction);
        void onError(String message);
    }

    /**
     * Interface callback chuyên dùng để phản hồi kết quả đếm số lượng giao dịch nháp.
     */
    public interface PendingCountCallback {
        void onSuccess(int count);
        void onError(String message);
    }

    /**
     * Interface callback đơn giản dùng cho các tác vụ thay đổi dữ liệu (thêm/xóa) không cần trả về thực thể.
     */
    public interface ActionCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Khởi tạo Repository bằng Application context.
     * 
     * @param application context của ứng dụng
     */
    public PendingTransactionRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        this.pendingTransactionDao = appDatabase.pendingTransactionDao();
        // Sử dụng SingleThreadExecutor để các lệnh truy vấn ghi/đọc SQLite diễn ra tuần tự, tránh tranh chấp dữ liệu
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = application.getApplicationContext();
    }

    /**
     * Thêm mới một giao dịch chờ duyệt vào cơ sở dữ liệu local (Room) bất đồng bộ.
     * 
     * @param pendingTransaction đối tượng giao dịch chờ duyệt cần lưu
     * @param callback phản hồi khi hoàn tất hoặc xảy ra lỗi
     */
    public void addPendingTransaction(PendingTransaction pendingTransaction, ActionCallback callback) {
        executorService.execute(() -> {
            try {
                pendingTransactionDao.insertPendingTransaction(pendingTransaction);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Không thể thêm giao dịch chờ duyệt: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Lấy danh sách toàn bộ giao dịch chờ duyệt bất đồng bộ.
     * 
     * @param callback trả về danh sách kết quả hoặc thông báo lỗi
     */
    public void getAllPendingTransactions(PendingTransactionCallback callback) {
        executorService.execute(() -> {
            try {
                List<PendingTransaction> list = pendingTransactionDao.getAllPendingTransactions();
                if (callback != null) {
                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Lỗi khi lấy danh sách giao dịch nháp: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Đếm số lượng giao dịch chờ duyệt hiện có bất đồng bộ.
     * 
     * @param callback trả về số lượng đếm được
     */
    public void getPendingCount(PendingCountCallback callback) {
        executorService.execute(() -> {
            try {
                int count = pendingTransactionDao.getPendingCount();
                if (callback != null) {
                    callback.onSuccess(count);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Lỗi khi đếm số lượng giao dịch nháp: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Xóa một giao dịch nháp khỏi cơ sở dữ liệu bất đồng bộ.
     * 
     * @param pendingTransaction đối tượng nháp cần xóa
     * @param callback phản hồi trạng thái hoàn thành
     */
    public void deletePendingTransaction(PendingTransaction pendingTransaction, ActionCallback callback) {
        executorService.execute(() -> {
            try {
                pendingTransactionDao.deletePendingTransaction(pendingTransaction);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Không thể xóa giao dịch nháp: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Xóa một giao dịch nháp theo ID của nó bất đồng bộ.
     * 
     * @param id khóa chính của giao dịch nháp cần xóa
     * @param callback phản hồi trạng thái hoàn thành
     */
    public void deletePendingTransactionById(String id, ActionCallback callback) {
        executorService.execute(() -> {
            try {
                pendingTransactionDao.deletePendingTransactionById(id);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Không thể xóa giao dịch nháp theo ID: " + e.getMessage());
                }
            }
        });
    }
}
