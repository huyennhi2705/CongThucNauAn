package com.example.congthucnauan.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
<<<<<<< HEAD
import android.util.Log;
=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
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

<<<<<<< HEAD
    private static final String TAG = "CategoryFragment";

=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    private RecyclerView recyclerCategory;
    private FloatingActionButton fabAddCategory;

    private CategoryRvAdapter adapter;
    private DatabaseReference categoryRef;
    private ValueEventListener categoryListener;

<<<<<<< HEAD
=======

>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
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

<<<<<<< HEAD
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
=======
        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        fabAddCategory = view.findViewById(R.id.buttonAdd);

        if (recyclerCategory == null) {
            Toast.makeText(requireContext(),
                    "Không tìm thấy recyclerCategory",
                    Toast.LENGTH_LONG).show();
            return;
        }

        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCategory.setHasFixedSize(false);

        adapter = new CategoryRvAdapter(
                R.layout.layout_item_category_grid,
                new CategoryRvAdapter.OnCategoryListener() {
                    @Override
                    public void onItemClick(Category category) {
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

                        Bundle args = new Bundle();
                        args.putString("categoryId", category.getId());

                        Navigation.findNavController(view)
                                .navigate(R.id.action_category_to_editCategory, args);
                    }

                    @Override
                    public void onDeleteClick(Category category) {
                        showDeleteDialog(category);
                    }
                }
        );

        recyclerCategory.setAdapter(adapter);
        loadCategories();

        if (fabAddCategory != null) {
            fabAddCategory.setOnClickListener(v ->
                    Navigation.findNavController(v)
                            .navigate(R.id.action_category_to_addCategory)
            );
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        }
    }

    private void loadCategories() {
        categoryListener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
<<<<<<< HEAD
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
=======
                ArrayList<Category> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);

                    if (category != null) {
                        if (TextUtils.isEmpty(category.getId())) {
                            category.setId(data.getKey());
                        }
                        list.add(category);
                    }
                }

                adapter.setCategories(list);
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
<<<<<<< HEAD
                Log.e(TAG, "Database error: " + error.getMessage());
=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                Toast.makeText(requireContext(),
                        "Lỗi tải danh mục: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(Category category) {
        if (category == null) return;

<<<<<<< HEAD
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
=======
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
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }
}