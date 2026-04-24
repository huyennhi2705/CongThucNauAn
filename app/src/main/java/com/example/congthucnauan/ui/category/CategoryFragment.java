package com.example.congthucnauan.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
        }
    }

    private void loadCategories() {
        categoryListener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Lỗi tải danh mục: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(Category category) {
        if (category == null) return;

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }
}