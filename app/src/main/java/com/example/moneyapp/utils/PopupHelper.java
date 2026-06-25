package com.example.moneyapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class PopupHelper {
    //region Interface
    public interface OnCalculatorResultListener {
        void onResult(double result);
    }

    public interface OnCurrencySelectedListener {
        void onSelected(String currencyCode);
    }
    public interface OnResourceSelectedListener {
        void onSelected(int id);
    }
    public interface OnGroupSelectedListener {
        void onSelected(String groupName, boolean isNewGroup);
    }
    //endregion

    // ====================================================================
    // HÀM NỘI BỘ (INTERNAL METHODS) - ĐÃ ĐƯỢC CHUẨN HÓA TÊN GỌI VÀ CHỨC NĂNG
    // ====================================================================

    private static void hideKeyboardSafely(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    private static void attachKeyboardDismissListener(BottomSheetDialog dialog, Context context) {
        dialog.setOnDismissListener(d -> hideKeyboardSafely(context));
    }

    private static View createBottomSheetHeaderView(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_selector_bottom_sheet, null);
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        tvTitle.setText(title);
        return view;
    }

    /**
     * Dùng cho các Popup có danh sách dài cần cuộn (Danh mục, Tiền tệ, Nhóm...)
     * Mở lên ở mốc 50% màn hình, cho phép kéo lên tối đa 85%.
     */
    private static void setupScrollableSheetBehavior(BottomSheetDialog dialog, Context context) {
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            int bottomSheetId = com.google.android.material.R.id.design_bottom_sheet;
            View bottomSheet = d.findViewById(bottomSheetId);

            if (bottomSheet != null) {
                ((View) bottomSheet.getParent()).setBackgroundColor(Color.TRANSPARENT);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);

                int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = (int) (screenHeight * 0.85); // Tối đa 85%
                bottomSheet.setLayoutParams(layoutParams);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(false);
                behavior.setPeekHeight((int) (screenHeight * 0.5)); // Mốc mặc định 50%
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        attachKeyboardDismissListener(dialog, context);
    }

    /**
     * Dùng cho Popup tĩnh, không cuộn được (Ví dụ: Máy tính).
     * Bỏ qua trạng thái Peek (50%), mở bung toàn bộ kích thước của View (Expanded).
     */
    private static void setupFixedSheetBehavior(BottomSheetDialog dialog, Context context) {
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            int bottomSheetId = com.google.android.material.R.id.design_bottom_sheet;
            View bottomSheet = d.findViewById(bottomSheetId);

            if (bottomSheet != null) {
                ((View) bottomSheet.getParent()).setBackgroundColor(Color.TRANSPARENT);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(true); // Bỏ qua mốc 50%
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Mở kịch cỡ content
            }
        });

        attachKeyboardDismissListener(dialog, context);
    }
    // ====================================================================

    //region Account
    public static void showAccountFilterPopup(Context context, List<Account> accountList,
                                              String currentAccountId,
                                              boolean showAllOption,
                                              AccountPopupAdapter.OnAccountClickListener listener) {
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBottomSheetHeaderView(context, "Chọn tài khoản");

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
        setupScrollableSheetBehavior(dialog, context);
        dialog.show();
    }
    //endregion

    //region Category
    public static void showCategoryFilterPopup(Context context,
                                               List<Category> categoryList,
                                               boolean showAllOption,
                                               CategoryAdapter.OnCategoryClickListener listener) {
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBottomSheetHeaderView(context, "Chọn hạng mục");

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
        setupScrollableSheetBehavior(dialog, context);
        dialog.show();
    }
    //endregion

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
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBottomSheetHeaderView(context, title);

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
        setupScrollableSheetBehavior(dialog, context);
        dialog.show();
    }

    private static void showPicker(Context context, boolean isColorPicker, String title, OnResourceSelectedListener listener) {
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBottomSheetHeaderView(context, title);

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
        setupScrollableSheetBehavior(dialog, context);
        dialog.show();
    }
    //endregion

    //region Calculator
    public static void showCalculatorPopup(Context context, String initialValue, OnCalculatorResultListener listener) {
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_calculator_sheet, null);

        TextView tvExpression = view.findViewById(R.id.tv_expression);
        Button btnAction = view.findViewById(R.id.btn_action);

        final String[] expr = {""};

        if (initialValue != null) {
            String cleanVal = initialValue.replaceAll("[.,đ\\s]", "");
            if (!cleanVal.isEmpty() && !cleanVal.equals("0")) {
                expr[0] = cleanVal;
            }
        }

        tvExpression.setText(expr[0].isEmpty() ? "0" : formatExpressionDisplay(expr[0]));
        if (expr[0].matches(".*[+\\-×÷].*")) btnAction.setText("=");
        else btnAction.setText("✔");

        View.OnClickListener clickListener = v -> {
            int id = v.getId();

            if (id == R.id.btn_clear) {
                DialogHelper.showConfirmDialog(context, "Xóa biểu thức", "Bạn có chắc chắn muốn xóa toàn bộ phép tính hiện tại không?", () -> {
                    expr[0] = "";
                    tvExpression.setText("0");
                    btnAction.setText("✔");
                }, null);
                return;
            }
            else if (id == R.id.btn_del) {
                if (!expr[0].isEmpty()) expr[0] = expr[0].substring(0, expr[0].length() - 1);
            }
            else if (id == R.id.btn_action) {
                if (expr[0].matches(".*[+\\-×÷].*")) {
                    try {
                        String mathStr = expr[0].replace("×", "*").replace("÷", "/");
                        double result = evalMath(mathStr);
                        if (result == (long) result) {
                            expr[0] = String.format(java.util.Locale.US, "%d", (long) result);
                        } else {
                            expr[0] = String.format(java.util.Locale.US, "%s", result);
                        }
                    } catch (Exception e) {
                        DialogHelper.showSimpleDialog(context, "Lỗi phép tính", "Cú pháp toán học không hợp lệ. Vui lòng kiểm tra lại.");
                    }
                } else {
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
                String val = ((Button) v).getText().toString();
                if (expr[0].equals("0") && (!val.equals(".") && !val.equals("000"))) {
                    expr[0] = "";
                }
                expr[0] += val;
            }

            tvExpression.setText(expr[0].isEmpty() ? "0" : formatExpressionDisplay(expr[0]));

            if (expr[0].matches(".*[+\\-×÷].*")) {
                btnAction.setText("=");
            } else {
                btnAction.setText("✔");
            }
        };

        int[] btnIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_000, R.id.btn_dot, R.id.btn_add, R.id.btn_sub, R.id.btn_mul, R.id.btn_div, R.id.btn_clear, R.id.btn_del, R.id.btn_action};
        for (int id : btnIds) {
            view.findViewById(id).setOnClickListener(clickListener);
        }

        dialog.setContentView(view);

        // ĐÃ SỬA: Máy tính dùng cấu hình tĩnh, không trượt 50-85%
        setupFixedSheetBehavior(dialog, context);

        dialog.show();
    }

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
            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    result.append(formatNumberPart(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
                result.append(" ").append(c).append(" ");
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
                return formattedInt + "," + parts[1];
            } else {
                long intPart = Long.parseLong(numStr);
                return String.format(java.util.Locale.US, "%,d", intPart).replace(",", ".");
            }
        } catch (Exception e) {
            return numStr;
        }
    }
    //endregion

    //region Currency
    public static void showCurrencyFilterPopup(Context context,
                                               List<String> currencyList,
                                               OnCurrencySelectedListener listener) {

        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_selector_currency, null);

        EditText etSearch = view.findViewById(R.id.et_search_currency);
        RecyclerView rvList = view.findViewById(R.id.rv_currency_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            private List<String> filteredList = new ArrayList<>(currencyList);

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(context);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(40, 40, 40, 40);
                tv.setTextSize(16f);
                tv.setTextColor(context.getResources().getColor(R.color.colorOnSurface, null));
                return new RecyclerView.ViewHolder(tv) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                String currency = filteredList.get(position);
                TextView tv = (TextView) holder.itemView;
                tv.setText(currency);

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(currency);
                    dialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return filteredList.size();
            }

            public void filter(String text) {
                filteredList.clear();
                if (text.isEmpty()) {
                    filteredList.addAll(currencyList);
                } else {
                    text = text.toLowerCase();
                    for (String item : currencyList) {
                        if (item.toLowerCase().contains(text)) {
                            filteredList.add(item);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        };

        rvList.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter.getClass().getMethod("filter", String.class).invoke(adapter, s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.setContentView(view);
        setupScrollableSheetBehavior(dialog, context);

        dialog.show();
    }
    //endregion

    //region Group Category
    public static void showGroupSelectorPopup(Context context,
                                              List<String> groupList,
                                              OnGroupSelectedListener listener) {
        hideKeyboardSafely(context);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBottomSheetHeaderView(context, "Chọn nhóm hạng mục");

        TextView tvAddNew = view.findViewById(R.id.tv_select_all);
        tvAddNew.setVisibility(View.VISIBLE);
        tvAddNew.setText("+ Thêm nhóm mới");
        tvAddNew.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.colorDanger));
        tvAddNew.setOnClickListener(v -> {
            if (listener != null) listener.onSelected("", true);
            dialog.dismiss();
        });

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(context);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(60, 40, 60, 40);
                tv.setTextSize(16f);
                tv.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.colorOnSurface));
                return new RecyclerView.ViewHolder(tv) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                String groupName = groupList.get(position);
                TextView tv = (TextView) holder.itemView;
                tv.setText(groupName);

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(groupName, false);
                    dialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return groupList.size();
            }
        };

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupScrollableSheetBehavior(dialog, context);
        dialog.show();
    }
    //endregion
}
