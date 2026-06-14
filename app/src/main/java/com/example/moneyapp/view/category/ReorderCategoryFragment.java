package com.example.moneyapp.view.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.List;

public class ReorderCategoryFragment extends BaseFragment {

    private RecyclerView rvReorder;
    private ReorderCategoryAdapter adapter;
    private CategoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reorder_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        setupHeader(view, "Sắp xếp hạng mục", true);

        rvReorder = view.findViewById(R.id.rv_reorder_categories);
        rvReorder.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && adapter == null) {
                adapter = new ReorderCategoryAdapter(categories);
                rvReorder.setAdapter(adapter);
                setupDragAndDrop();
            }
        });
        
        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Đã cập nhật thứ tự", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                adapter.onItemMove(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not supported
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // When drop is finished, update orders on server
                saveNewOrders();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvReorder);
    }

    private void saveNewOrders() {
        List<Category> categories = adapter.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            viewModel.reorderCategory(cat, i);
        }
    }

    @Override
    protected String getFabIcon() {
        return "gmd_check";
    }

    @Override
    protected void onFabClick() {
        requireActivity().onBackPressed();
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}
