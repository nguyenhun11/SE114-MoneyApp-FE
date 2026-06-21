package com.example.moneyapp.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.view.account.AccountPopupAdapter;
import com.example.moneyapp.view.category.CategoryAdapter;
import com.example.moneyapp.view.category.CategoryGroupAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class PopupHelper {
    public interface OnCalculatorResultListener {
        void onResult(double result);
    }
    private static View createBaseSheetView(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_selector_bottom_sheet, null);
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        tvTitle.setText(title);
        return view;
    }

    public static void showAccountFilterPopup(Context context, List<Account> accountList,
                                              String currentAccountId,
                                              boolean showAllOption,
                                              AccountPopupAdapter.OnAccountClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBaseSheetView(context, "Chọn tài khoản");

        TextView tvSelectAll = view.findViewById(R.id.tv_select_all);
        if (showAllOption) {
            tvSelectAll.setVisibility(View.VISIBLE);
            tvSelectAll.setText("Tất cả tài khoản");
            tvSelectAll.setOnClickListener(v -> {
                if (listener != null) listener.onAccountClick(null);
                dialog.dismiss();
            });
        }

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        AccountPopupAdapter adapter = new AccountPopupAdapter(accountList, currentAccountId, account -> {
            if (listener != null) listener.onAccountClick(account);
            dialog.dismiss();
        });
        rvList.setAdapter(adapter);

        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }

    public static void showCategoryFilterPopup(Context context,
                                               List<Category> categoryList,
                                               boolean showAllOption,
                                               CategoryAdapter.OnCategoryClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBaseSheetView(context, "Chọn hạng mục");

        TextView tvSelectAll = view.findViewById(R.id.tv_select_all);
        if (showAllOption) {
            tvSelectAll.setVisibility(View.VISIBLE);
            tvSelectAll.setText("Tất cả hạng mục");
            tvSelectAll.setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(null);
                dialog.dismiss();
            });
        }

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        CategoryGroupAdapter adapter = new CategoryGroupAdapter(category -> {
            if (listener != null) listener.onCategoryClick(category);
            dialog.dismiss();
        });
        adapter.setData(categoryList);

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }
    public interface OnResourceSelectedListener {
        void onSelected(int id);
    }

    //region Color & Icon
    public static void showColorPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, true, "Chọn màu sắc", listener);
    }

    public static void showIconPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, false, "Chọn biểu tượng", listener);
    }

    public static void showGoalIconPicker(Context context, OnResourceSelectedListener listener) {
        showGoalPicker(context, "Chọn biểu tượng mục tiêu", listener);
    }

    private static void showGoalPicker(Context context, String title, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBaseSheetView(context, title);

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new GridLayoutManager(context, 4));

        int startIdx = AppResourceManager.getGoalIconStart();
        int count = AppResourceManager.getIconCount() - startIdx;

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return count;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_selector_icon, parent, false);
                return new RecyclerView.ViewHolder(itemView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                int actualPos = startIdx + position;
                IconicsImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                ivIcon.setImageDrawable(AppResourceManager.getBlackIcon(context, actualPos));

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(actualPos);
                    dialog.dismiss();
                });
            }
        };

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }

    private static void showPicker(Context context, boolean isColorPicker, String title, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBaseSheetView(context, title);

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new GridLayoutManager(context, 4));

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return isColorPicker ? AppResourceManager.getColorCount() : AppResourceManager.getIconCount();
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                int layoutId = isColorPicker ? R.layout.item_selector_color : R.layout.item_selector_icon;
                View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
                return new RecyclerView.ViewHolder(itemView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (isColorPicker) {
                    View colorCircle = holder.itemView.findViewById(R.id.view_color);
                    int colorValue = AppResourceManager.getColor(position);
                    colorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));
                } else {
                    IconicsImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                    String iconName = AppResourceManager.getIconName(position);
                    ivIcon.setIcon(new IconicsDrawable(context, iconName));
                }

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(position);
                    dialog.dismiss();
                });
            }
        };

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }
    //endregion

    //region Calculator
    public static void showCalculatorPopup(Context context, String initialValue, OnCalculatorResultListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_calculator_sheet, null);

        TextView tvExpression = view.findViewById(R.id.tv_expression);
        Button btnAction = view.findViewById(R.id.btn_action);

        final String[] expr = {""};

        // Xử lý nạp số tiền hiện tại từ ô nhập liệu vào máy tính
        if (initialValue != null) {
            String cleanVal = initialValue.replaceAll("[.,đ\\s]", "");
            if (!cleanVal.isEmpty() && !cleanVal.equals("0")) {
                expr[0] = cleanVal;
            }
        }

        // Cập nhật UI ngay khi vừa nạp
        tvExpression.setText(expr[0].isEmpty() ? "0" : formatExpressionDisplay(expr[0]));
        if (expr[0].matches(".*[+\\-×÷].*")) btnAction.setText("=");
        else btnAction.setText("✔");

        View.OnClickListener clickListener = v -> {
            int id = v.getId();

            if (id == R.id.btn_clear) {
                expr[0] = "";
            }
            else if (id == R.id.btn_del) {
                if (!expr[0].isEmpty()) expr[0] = expr[0].substring(0, expr[0].length() - 1);
            }
            else if (id == R.id.btn_action) {
                // TỰ QUYẾT ĐỊNH HÀNH ĐỘNG DỰA VÀO BIỂU THỨC
                if (expr[0].matches(".*[+\\-×÷].*")) {
                    // Đang có phép tính -> Thực hiện tính toán (=)
                    try {
                        String mathStr = expr[0].replace("×", "*").replace("÷", "/");
                        double result = evalMath(mathStr);
                        if (result == (long) result) {
                            expr[0] = String.format(java.util.Locale.US, "%d", (long) result);
                        } else {
                            expr[0] = String.format(java.util.Locale.US, "%s", result);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Phép tính lỗi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Chỉ có số -> Trả kết quả về cho Fragment (✔)
                    if (listener != null) {
                        try {
                            listener.onResult(Double.parseDouble(expr[0]));
                        } catch (Exception e) {
                            listener.onResult(0);
                        }
                    }
                    dialog.dismiss();
                    return;
                }
            }
            else if (v instanceof Button) {
                // Xử lý các nút số và dấu phép tính
                String val = ((Button) v).getText().toString();
                if (expr[0].equals("0") && (!val.equals(".") && !val.equals("000"))) {
                    expr[0] = "";
                }
                expr[0] += val;
            }

            // GỌI HÀM FORMAT HIỂN THỊ CÁCH 3 SỐ
            tvExpression.setText(expr[0].isEmpty() ? "0" : formatExpressionDisplay(expr[0]));

            // Cập nhật giao diện của nút Action
            if (expr[0].matches(".*[+\\-×÷].*")) {
                btnAction.setText("=");
            } else {
                btnAction.setText("✔");
            }
        };

        // Gắn sự kiện cho toàn bộ nút
        int[] btnIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_000, R.id.btn_dot, R.id.btn_add, R.id.btn_sub, R.id.btn_mul, R.id.btn_div, R.id.btn_clear, R.id.btn_del, R.id.btn_action};
        for (int id : btnIds) {
            view.findViewById(id).setOnClickListener(clickListener);
        }

        dialog.setContentView(view);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                ((View) bottomSheet.getParent()).setBackgroundColor(android.graphics.Color.TRANSPARENT);
                bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        dialog.show();
    }

    // Thuật toán parse toán học mini
    private static double evalMath(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Lỗi");
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Lỗi");
                }
                return x;
            }
        }.parse();
    }

    private static String formatExpressionDisplay(String mathExpr) {
        if (mathExpr.isEmpty()) return "0";
        StringBuilder result = new StringBuilder();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < mathExpr.length(); i++) {
            char c = mathExpr.charAt(i);
            if (Character.isDigit(c) || c == '.') { // Giữ nguyên dấu thập phân
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    result.append(formatNumberPart(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
                result.append(" ").append(c).append(" "); // Thêm khoảng trắng quanh phép tính cho đẹp
            }
        }
        if (currentNumber.length() > 0) {
            result.append(formatNumberPart(currentNumber.toString()));
        }
        return result.toString();
    }

    private static String formatNumberPart(String numStr) {
        try {
            if (numStr.contains(".")) {
                String[] parts = numStr.split("\\.");
                long intPart = Long.parseLong(parts[0].isEmpty() ? "0" : parts[0]);
                String formattedInt = String.format(java.util.Locale.US, "%,d", intPart).replace(",", ".");
                return formattedInt + "," + parts[1]; // VN dùng phẩy cho thập phân
            } else {
                long intPart = Long.parseLong(numStr);
                return String.format(java.util.Locale.US, "%,d", intPart).replace(",", "."); // Ngăn cách ngàn bằng chấm
            }
        } catch (Exception e) {
            return numStr; // Fallback
        }
    }
    //endregion

    private static void setupBottomSheetBehavior(BottomSheetDialog dialog, Context context) {
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                // FIX LỖI VIỀN TRẮNG CHO CÁC POPUP KHÁC (TÀI KHOẢN, HẠNG MỤC...)
                ((View) bottomSheet.getParent()).setBackgroundColor(Color.TRANSPARENT);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);

                int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = (int) (screenHeight * 0.85);
                bottomSheet.setLayoutParams(layoutParams);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight((int) (screenHeight * 0.5));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }
}