package com.example.congthucnauan.ui.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryFragment extends Fragment {

    private static final String TAG = "AddCategoryFragment";

    // ── Firebase ──
    // Trỏ thẳng vào database URL của dự án, node "categories"
    private static final String DB_URL = "https://ctna-996dc-default-rtdb.firebaseio.com/";
    private DatabaseReference categoryRef;

    // ── Views ──
    private ImageButton btnBack;
    private TextInputEditText etName, etDescription;
    private TextView tvSelectedIcon;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    // Icon đang được chọn (lưu tên drawable hoặc emoji key)
    private String selectedIcon = "ic_default_food";

    // Danh sách icon gợi ý (emoji + tên drawable tương ứng)
    private static final String[] ICON_LABELS   = {"🍚", "🥗", "🍰", "🥤", "🍜", "🍟","🍳","🍲"};
    private static final String[] ICON_DRAWABLE = {
            "ic_food_main",   "ic_food_salad",    "ic_food_dessert",
            "ic_drinks",  "ic_dish_of_water", "ic_snacks", "ic_fried", "ic_hotpot"
    };


    private boolean isEditMode = false;
    private String  editCategoryId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Firebase reference đến node "categories"
        categoryRef = FirebaseDatabase.getInstance(DB_URL).getReference("categories");

        bindViews(view);
        setupIconPicker(view);
        checkEditMode();   // Xác định đang thêm mới hay sửa
        setupListeners(view);
    }


    private void bindViews(View view) {
        btnBack         = view.findViewById(R.id.btnBack);
        etName          = view.findViewById(R.id.etCategoryName);
        etDescription   = view.findViewById(R.id.etCategoryDescription);
        tvSelectedIcon  = view.findViewById(R.id.tvSelectedIcon);
        btnSave         = view.findViewById(R.id.btnSaveCategory);
        progressBar     = view.findViewById(R.id.progressBar);
    }


    private void setupIconPicker(View view) {
        // Lấy container icon (LinearLayout ngang cuộn được)
        ViewGroup iconContainer = view.findViewById(R.id.iconPickerContainer);
        if (iconContainer == null) return;

        for (int i = 0; i < ICON_LABELS.length; i++) {
            final int idx = i;
            TextView iconView = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_icon_picker, iconContainer, false);
            iconView.setText(ICON_LABELS[i]);

            iconView.setOnClickListener(v -> {
                selectedIcon = ICON_DRAWABLE[idx];
                if (tvSelectedIcon != null) {
                    tvSelectedIcon.setText(ICON_LABELS[idx]);
                }
                // Highlight icon được chọn
                for (int j = 0; j < iconContainer.getChildCount(); j++) {
                    iconContainer.getChildAt(j).setSelected(false);
                }
                v.setSelected(true);
                Log.d(TAG, "Icon selected: " + selectedIcon);
            });

            iconContainer.addView(iconView);
        }

        // Mặc định chọn icon đầu tiên
        if (iconContainer.getChildCount() > 0) {
            iconContainer.getChildAt(0).setSelected(true);
            selectedIcon = ICON_DRAWABLE[0];
            if (tvSelectedIcon != null) tvSelectedIcon.setText(ICON_LABELS[0]);
        }
    }

    private void checkEditMode() {
        Bundle args = getArguments();
        if (args == null) return;

        editCategoryId = args.getString("categoryId");
        if (editCategoryId != null && !editCategoryId.isEmpty()) {
            isEditMode = true;

            // Điền sẵn dữ liệu vào form
            String name = args.getString("categoryName", "");
            String desc = args.getString("categoryDescription", "");
            String icon = args.getString("categoryIcon", ICON_DRAWABLE[0]);

            if (etName        != null) etName.setText(name);
            if (etDescription != null) etDescription.setText(desc);
            selectedIcon = icon;

            // Cập nhật tiêu đề nút
            if (btnSave != null) btnSave.setText("Cập nhật danh mục");

            // Cập nhật header title nếu có
            Log.d(TAG, "Edit mode: categoryId = " + editCategoryId);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Listeners
    // ─────────────────────────────────────────────────────────────
    private void setupListeners(View view) {
        // Nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    Navigation.findNavController(v).popBackStack());
        }

        // Nút lưu / cập nhật
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                if (validateInput()) {
                    if (isEditMode) {
                        updateCategory(v);
                    } else {
                        saveCategory(v);
                    }
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Validate dữ liệu nhập
    // ─────────────────────────────────────────────────────────────
    private boolean validateInput() {
        if (etName == null) return false;

        String name = etName.getText() != null
                ? etName.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên danh mục");
            etName.requestFocus();
            return false;
        }

        if (name.length() < 2) {
            etName.setError("Tên danh mục phải có ít nhất 2 ký tự");
            etName.requestFocus();
            return false;
        }

        return true;
    }

    // ─────────────────────────────────────────────────────────────
    //  THÊM MỚI danh mục → Firebase /categories/{pushKey}
    // ─────────────────────────────────────────────────────────────
    private void saveCategory(View navView) {
        String name = etName.getText().toString().trim();
        String desc = (etDescription != null && etDescription.getText() != null)
                ? etDescription.getText().toString().trim()
                : "";

        // Tạo key mới bằng Firebase push() — tự sinh ID duy nhất
        String newId = categoryRef.push().getKey();
        if (newId == null) {
            Toast.makeText(requireContext(), "Lỗi tạo ID, thử lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(newId, name, desc, selectedIcon);

        setLoading(true);

        categoryRef.child(newId)
                .setValue(category)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    Log.d(TAG, "Category saved: " + newId);
                    Toast.makeText(requireContext(),
                            "✅ Đã thêm danh mục \"" + name + "\"",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(navView).popBackStack();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    Log.e(TAG, "Save failed: " + e.getMessage());
                    Toast.makeText(requireContext(),
                            "❌ Lỗi lưu: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // ─────────────────────────────────────────────────────────────
    //  CẬP NHẬT danh mục đã tồn tại
    // ─────────────────────────────────────────────────────────────
    private void updateCategory(View navView) {
        String name = etName.getText().toString().trim();
        String desc = (etDescription != null && etDescription.getText() != null)
                ? etDescription.getText().toString().trim()
                : "";

        setLoading(true);

        categoryRef.child(editCategoryId).child("name").setValue(name);
        categoryRef.child(editCategoryId).child("description").setValue(desc);
        categoryRef.child(editCategoryId).child("icon")
                .setValue(selectedIcon)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    Log.d(TAG, "Category updated: " + editCategoryId);
                    Toast.makeText(requireContext(),
                            "✅ Đã cập nhật danh mục \"" + name + "\"",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(navView).popBackStack();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    Log.e(TAG, "Update failed: " + e.getMessage());
                    Toast.makeText(requireContext(),
                            "❌ Lỗi cập nhật: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // ─────────────────────────────────────────────────────────────
    //  Hiện / ẩn loading
    // ─────────────────────────────────────────────────────────────
    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        if (btnSave != null) {
            btnSave.setEnabled(!loading);
            btnSave.setText(loading ? "Đang lưu..." : (isEditMode ? "Cập nhật danh mục" : "Lưu danh mục"));
        }
    }
}