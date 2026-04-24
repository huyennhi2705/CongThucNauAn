package com.example.congthucnauan.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.CategoryRvAdapter;
import com.example.congthucnauan.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";

    private RecyclerView recyclerCategory;
    private FloatingActionButton fabAddCategory;

    private CategoryRvAdapter adapter;
    private DatabaseReference categoryRef;
    private ValueEventListener categoryListener;

    public CategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            recyclerCategory = view.findViewById(R.id.recyclerCategory);
            fabAddCategory = view.findViewById(R.id.buttonAdd);

            // ✅ Kiểm tra RecyclerView
            if (recyclerCategory == null) {
                Log.e(TAG, "RecyclerView recyclerCategory not found!");
                Toast.makeText(requireContext(),
                        "Không tìm thấy RecyclerView",
                        Toast.LENGTH_LONG).show();
                return;
            }

            categoryRef = FirebaseDatabase.getInstance().getReference("categories");

            recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerCategory.setHasFixedSize(false);

            // ✅ Khởi tạo adapter với R.layout.item_home_category
            adapter = new CategoryRvAdapter(
                    R.layout.item_home_category,
                    new CategoryRvAdapter.OnCategoryListener() {
                        @Override
                        public void onItemClick(Category category) {
                            if (category == null) {
                                Toast.makeText(requireContext(),
                                        "Danh mục không hợp lệ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(requireContext(),
                                    "Chọn: " + category.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onEditClick(Category category) {
                            if (category == null || TextUtils.isEmpty(category.getId())) {
                                Toast.makeText(requireContext(),
                                        "Không tìm thấy ID danh mục để sửa!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                Bundle args = new Bundle();
                                args.putString("categoryId", category.getId());

                                Navigation.findNavController(recyclerCategory)
                                        .navigate(R.id.action_adminCategory_to_editCategory, args);
                            } catch (Exception e) {
                                Log.e(TAG, "Navigation error: " + e.getMessage());
                                Toast.makeText(requireContext(),
                                        "Lỗi: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onDeleteClick(Category category) {
                            showDeleteDialog(category);
                        }
                    }
            );

            recyclerCategory.setAdapter(adapter);
            loadCategories();

            // ✅ Set FAB click listener
            if (fabAddCategory != null) {
                fabAddCategory.setOnClickListener(v -> {
                    try {
                        Navigation.findNavController(v)
                                .navigate(R.id.action_category_to_addCategory);
                    } catch (Exception e) {
                        Log.e(TAG, "FAB Navigation error: " + e.getMessage());
                        Toast.makeText(requireContext(),
                                "Không thể điều hướng",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        categoryListener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ArrayList<Category> list = new ArrayList<>();

                    for (DataSnapshot data : snapshot.getChildren()) {
                        Category category = data.getValue(Category.class);

                        if (category != null) {
                            // ✅ Gán ID nếu chưa có
                            if (TextUtils.isEmpty(category.getId())) {
                                category.setId(data.getKey());
                            }
                            list.add(category);
                        }
                    }

                    // ✅ Update adapter an toàn
                    if (adapter != null) {
                        adapter.setCategories(list);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading categories: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(requireContext(),
                        "Lỗi tải danh mục: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(Category category) {
        if (category == null) return;

        try {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xoá danh mục")
                    .setMessage("Bạn có chắc muốn xoá \"" + category.getName() + "\" không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        if (TextUtils.isEmpty(category.getId())) {
                            Toast.makeText(requireContext(),
                                    "Không tìm thấy ID danh mục!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        categoryRef.child(category.getId())
                                .removeValue()
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(requireContext(),
                                                "Đã xoá danh mục!",
                                                Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(),
                                                "Lỗi xoá: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete dialog: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }
}