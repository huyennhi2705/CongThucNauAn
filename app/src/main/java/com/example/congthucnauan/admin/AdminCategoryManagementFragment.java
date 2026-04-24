package com.example.congthucnauan.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryManagementFragment extends Fragment {

    private static final String TAG    = "AdminCategoryMgmt";
    private static final String DB_URL = "https://ctna-996dc-default-rtdb.firebaseio.com/";

    // ── Views ──
    private View                 rootView;
    private RecyclerView         recyclerCategories;
    private LinearLayout         emptyLayout;
    private FloatingActionButton fabAddCategory;
    private ImageButton          btnBack;

    // ── Firebase ──
    private DatabaseReference  categoryRef;
    private ValueEventListener categoryListener;

    // ── Data & Adapter ──
    private final List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter      adapter;

    // ══════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.fragment_admin_category_management, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        categoryRef = FirebaseDatabase.getInstance(DB_URL).getReference("categories");

        bindViews(view);
        setupRecyclerView();
        setupListeners(view);
        loadCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Setup
    // ══════════════════════════════════════════════════════════

    private void bindViews(View view) {
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        emptyLayout        = view.findViewById(R.id.emptyLayout);
        fabAddCategory     = view.findViewById(R.id.fabAddCategory);
        btnBack            = view.findViewById(R.id.btnBack);
        Log.d(TAG, "Views bound");
    }

    private void setupRecyclerView() {
        recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnActionListener() {
            @Override
            public void onEdit(Category category) {
                navigateToEditCategory(category);
            }

            @Override
            public void onDelete(Category category) {
                confirmDelete(category);
            }
        });

        recyclerCategories.setAdapter(adapter);
        Log.d(TAG, "RecyclerView setup done");
    }

    private void setupListeners(View view) {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }

        if (fabAddCategory != null) {
            fabAddCategory.setOnClickListener(v -> navigateToAddCategory(v));
        }

        View btnCreateFirst = emptyLayout != null
                ? emptyLayout.findViewById(R.id.btnCreateFirst) : null;
        if (btnCreateFirst != null) {
            btnCreateFirst.setOnClickListener(v -> navigateToAddCategory(v));
        }

        View btnFilter = view.findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v ->
                    Toast.makeText(requireContext(),
                            "Chức năng tìm kiếm sắp ra mắt",
                            Toast.LENGTH_SHORT).show());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Firebase: Load
    // ══════════════════════════════════════════════════════════

    private void loadCategories() {
        Log.d(TAG, "Loading categories...");

        categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                categoryList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Category cat = child.getValue(Category.class);
                        if (cat != null) {
                            if (cat.getId() == null) cat.setId(child.getKey());
                            categoryList.add(cat);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                    }
                }

                updateUI();
                Log.d(TAG, "Loaded " + categoryList.size() + " categories");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Log.e(TAG, "DB error: " + error.getMessage());
                Toast.makeText(requireContext(),
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        categoryRef.addValueEventListener(categoryListener);
    }

    // ══════════════════════════════════════════════════════════
    //  Firebase: Xoá
    // ══════════════════════════════════════════════════════════

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá danh mục")
                .setMessage("Bạn có chắc muốn xoá \"" + category.getName()
                        + "\" không?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xoá", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteCategory(Category category) {
        if (category.getId() == null) {
            Toast.makeText(requireContext(),
                    "Không xác định được ID danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        categoryRef.child(category.getId())
                .removeValue()
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "🗑️ Đã xoá \"" + category.getName() + "\"",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Deleted: " + category.getId());
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "❌ Lỗi xoá: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // ══════════════════════════════════════════════════════════
    //  Navigation
    // ══════════════════════════════════════════════════════════

    private void navigateToAddCategory(View v) {
        try {
            Navigation.findNavController(v)
                    .navigate(R.id.action_adminCategory_to_addCategory);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: " + e.getMessage());
            Toast.makeText(requireContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToEditCategory(Category category) {
        try {
            Bundle args = new Bundle();
            args.putString("categoryId",          category.getId());
            args.putString("categoryName",        category.getName());
            args.putString("categoryDescription", category.getDescription());
            args.putString("categoryIcon",        category.getIcon());

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_adminCategory_to_addCategory, args);
        } catch (Exception e) {
            Log.e(TAG, "Edit navigation error: " + e.getMessage());
            Toast.makeText(requireContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  UI update
    // ══════════════════════════════════════════════════════════

    private void updateUI() {
        if (!isAdded()) return;

        if (categoryList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerCategories.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerCategories.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Inner Adapter
    // ══════════════════════════════════════════════════════════

    public static class CategoryAdapter
            extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        public interface OnActionListener {
            void onEdit(Category category);
            void onDelete(Category category);
        }

        private final List<Category>   items;
        private final OnActionListener listener;

        public CategoryAdapter(List<Category> items, OnActionListener listener) {
            this.items    = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Category cat = items.get(pos);

            // ── Tên danh mục ──
            if (h.tvName != null) {
                h.tvName.setText(cat.getName() != null ? cat.getName() : "");
            }

            // ── Số công thức (placeholder, có thể cập nhật sau) ──
            if (h.tvRecipeCount != null) {
                h.tvRecipeCount.setText("0 công thức");
            }

            // ── ✅ FIX: Map iconName → emoji và hiển thị vào tvEmoji ──
            if (h.tvEmoji != null) {
                h.tvEmoji.setText(getEmojiForIcon(cat.getIcon()));
            }

            // ── Nút sửa ──
            if (h.btnEdit != null) {
                h.btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(cat);
                });
            }

            // ── Nút xoá ──
            if (h.btnDelete != null) {
                h.btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(cat);
                });
            }
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        /**
         * Map iconName lưu trong Firebase → emoji hiển thị.
         * Phải khớp với ICON_DRAWABLE[] trong AddCategoryFragment.
         */
        private static String getEmojiForIcon(String iconName) {
            if (iconName == null || iconName.isEmpty()) return "🍽️";
            switch (iconName) {
                case "ic_food_main":      return "🍚";
                case "ic_food_salad":     return "🥗";
                case "ic_food_dessert":   return "🍰";
                case "ic_drinks":         return "🥤";
                case "ic_dish_of_water":  return "🍜";
                case "ic_snacks":         return "🍟";
                case "ic_fried":          return "🍳";
                case "ic_hotpot":         return "🍲";
                // Tương thích với các tên cũ (nếu có data cũ trong Firebase)
                case "ic_food_drink":     return "🥤";
                case "ic_food_breakfast": return "🥐";
                case "ic_food_soup":      return "🍜";
                case "ic_food_grill":     return "🥩";
                case "ic_food_rice":      return "🍚";
                case "ic_food_snack":     return "🥗";
                default:                  return "🍽️";
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView    tvName, tvRecipeCount, tvEmoji;
            ImageButton btnEdit, btnDelete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName        = itemView.findViewById(R.id.tvName);
                tvRecipeCount = itemView.findViewById(R.id.tvRecipeCount);
                tvEmoji       = itemView.findViewById(R.id.tvEmoji);   // ✅ bind tvEmoji
                btnEdit       = itemView.findViewById(R.id.btnEdit);
                btnDelete     = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}