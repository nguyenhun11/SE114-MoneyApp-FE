//package com.example.moneyapp.data.local;
//
//import android.app.Application;
//import com.example.moneyapp.data.local.dao.AccountDao;
//import com.example.moneyapp.data.local.dao.CategoryDao;
////import com.example.moneyapp.data.local.dao.UserDao;
//import com.example.moneyapp.data.local.entity.Account;
//import com.example.moneyapp.data.local.entity.Category;
////import com.example.moneyapp.data.local.entity.User;
//import com.example.moneyapp.data.local.PreferenceManager;
//import java.util.concurrent.Executors;
//
//public class DatabaseSeeder {
//
//    public static void seedIfEmpty(Application application) {
//        Executors.newSingleThreadExecutor().execute(() -> {
//            AppDatabase db = AppDatabase.getInstance(application);
//            String userId = PreferenceManager.getInstance(application).getUserID();
//
//            if (userId == null || userId.isEmpty()) return;
//
//            // Kiểm tra xem user có tồn tại trong DB không để tránh lỗi Foreign Key
//            //UserDao userDao = db.userDao();
//            //User currentUser = userDao.getUserById(userId);
//            if (currentUser == null) {
//                // Nếu user không tồn tại trong local DB, không thể seed các bảng phụ thuộc
//                return;
//            }
//
//            seedAccounts(db.accountDao(), userId);
//            seedCategories(db.categoryDao(), userId);
//        });
//    }
//
//    private static void seedAccounts(AccountDao dao, String userId) {
//        if (dao.countByUserId(userId) > 0) return; // đã có rồi, bỏ qua
//
//        dao.insertAccount(new Account(userId, "Tiền mặt", 0.0, "ic_cash",    "#4CAF50"));
//        dao.insertAccount(new Account(userId, "Momo",     0.0, "ic_momo",    "#E91E8C"));
//        dao.insertAccount(new Account(userId, "Ngân hàng",0.0, "ic_bank",    "#2196F3"));
//    }
//
//    private static void seedCategories(CategoryDao dao, String userId) {
//        if (dao.countByUserId(userId) > 0) return; // đã có rồi, bỏ qua
//
//        // type: 2 = expense (chi)
//        dao.insertCategory(new Category(userId, "Ăn uống",   0.0, "ic_food",     "#FF9800", "Sinh hoạt", 2, true, false));
//        dao.insertCategory(new Category(userId, "Sinh hoạt", 0.0, "ic_home",     "#9C27B0", "Sinh hoạt", 2, true, false));
//        dao.insertCategory(new Category(userId, "Di chuyển", 0.0, "ic_transport","#2196F3", "Di chuyển", 2, true, false));
//        dao.insertCategory(new Category(userId, "Mua sắm",   0.0, "ic_shopping", "#E91E63", "Sinh hoạt", 2, true, false));
//        dao.insertCategory(new Category(userId, "Giải trí",  0.0, "ic_entertain","#607D8B", "Giải trí",  2, true, false));
//
//        // type: 1 = income (thu)
//        dao.insertCategory(new Category(userId, "Lương",     0.0, "ic_salary",   "#4CAF50", "Thu nhập",  1, true, false));
//        dao.insertCategory(new Category(userId, "Thưởng",    0.0, "ic_bonus",    "#FF5722", "Thu nhập",  1, true, false));
//        dao.insertCategory(new Category(userId, "Khác",      0.0, "ic_other",    "#9E9E9E", "Khác",      1, true, false));
//    }
//}
