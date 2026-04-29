package com.example.congthucnauan.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditFragment extends Fragment {

    private TextInputEditText edtName, edtDescription;
    private ImageView imgIcon;
    private TextView txtIconLabel;
    private FloatingActionButton btnSave;
    private MaterialToolbar toolbar;
    private View cardIconPicker;

    private DatabaseReference categoryRef;

    private String categoryId = "";
    private String selectedIcon = "monanchinh";
<<<<<<< HEAD

    // ICON DATA
=======
    private String categoryType = "Danh mục";

    // Tên icon lưu Firebase
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    private final String[] ICON_NAMES = {
            "monanchinh",
            "montrangmieng",
            "monannuoc",
            "monnuoc"
    };

<<<<<<< HEAD
=======
    // Tên hiển thị
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    private final String[] ICON_LABELS = {
            "Món ăn chính",
            "Món tráng miệng",
            "Món ăn nước",
            "Món nước"
    };

<<<<<<< HEAD
=======
    // Drawable tương ứng
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    private final int[] ICON_RES = {
            R.drawable.monanchinh,
            R.drawable.montrangmieng,
            R.drawable.monannuoc,
            R.drawable.monnuoc
    };

<<<<<<< HEAD
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
=======
    public EditFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_category, container, false);
    }

    @Override
<<<<<<< HEAD
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId", "");
        }

        if (TextUtils.isEmpty(categoryId)) {
            Toast.makeText(requireContext(),
                    "Không tìm thấy ID danh mục!",
                    Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );

        cardIconPicker.setOnClickListener(v -> showIconPickerDialog());

        loadCategoryData();

        btnSave.setOnClickListener(v -> updateCategory());
    }

    private void bindViews(View view) {
=======
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        edtName = view.findViewById(R.id.edtName);
        edtDescription = view.findViewById(R.id.edtDescription);
        imgIcon = view.findViewById(R.id.imgIcon);
        txtIconLabel = view.findViewById(R.id.txtIconLabel);
        btnSave = view.findViewById(R.id.buttonSave);
        toolbar = view.findViewById(R.id.toolbarTop);
        cardIconPicker = view.findViewById(R.id.cardIconPicker);
<<<<<<< HEAD
    }

    // ================= LOAD DATA =================
    private void loadCategoryData() {
        categoryRef.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Category category = snapshot.getValue(Category.class);

                        if (category == null) {
                            Toast.makeText(requireContext(),
                                    "Không tìm thấy dữ liệu!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        edtName.setText(category.getName());
                        edtDescription.setText(category.getDescription());

                        if (!TextUtils.isEmpty(category.getIcon())) {
                            selectedIcon = category.getIcon();
                        }

                        int index = getIconIndex(selectedIcon);

                        if (index != -1) {
                            imgIcon.setImageResource(ICON_RES[index]);
                            txtIconLabel.setText(ICON_LABELS[index]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= ICON PICKER =================
    private void showIconPickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn icon")
=======

        // Firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        // Nhận ID từ Bundle
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId", "");
        }

        if (TextUtils.isEmpty(categoryId)) {
            Toast.makeText(requireContext(), "Không tìm thấy ID danh mục!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        // Toolbar back
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );

        // Chọn icon
        cardIconPicker.setOnClickListener(v -> showIconPickerDialog());

        // Load dữ liệu cũ
        loadCategoryData();

        // Lưu cập nhật
        btnSave.setOnClickListener(v -> updateCategory());
    }

    private void loadCategoryData() {
        categoryRef.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);

                if (category == null) {
                    Toast.makeText(requireContext(), "Không tìm thấy dữ liệu danh mục!", Toast.LENGTH_SHORT).show();
                    return;
                }

                edtName.setText(category.getName() != null ? category.getName() : "");
                edtDescription.setText(category.getDescription() != null ? category.getDescription() : "");

                if (!TextUtils.isEmpty(category.getType())) {
                    categoryType = category.getType();
                }

                if (!TextUtils.isEmpty(category.getIcon())) {
                    selectedIcon = category.getIcon();
                }

                int index = getIconIndex(selectedIcon);
                if (index != -1) {
                    imgIcon.setImageResource(ICON_RES[index]);
                    txtIconLabel.setText(ICON_LABELS[index]);
                } else {
                    imgIcon.setImageResource(R.drawable.ic_launcher_foreground);
                    txtIconLabel.setText("Nhấn để chọn icon");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIconPickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn icon cho danh mục")
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                .setItems(ICON_LABELS, (dialog, which) -> {
                    selectedIcon = ICON_NAMES[which];
                    imgIcon.setImageResource(ICON_RES[which]);
                    txtIconLabel.setText(ICON_LABELS[which]);
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

<<<<<<< HEAD
    // ================= UPDATE CATEGORY =================
    private void updateCategory() {

        String name = edtName.getText() != null
                ? edtName.getText().toString().trim()
                : "";

        String desc = edtDescription.getText() != null
                ? edtDescription.getText().toString().trim()
                : "";
=======
    private void updateCategory() {
        String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
        String desc = edtDescription.getText() != null ? edtDescription.getText().toString().trim() : "";
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Vui lòng nhập tên danh mục");
            edtName.requestFocus();
            return;
        }

        Category updatedCategory = new Category(
                categoryId,
<<<<<<< HEAD
=======
                categoryType,
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                name,
                desc,
                selectedIcon
        );

        categoryRef.child(categoryId).setValue(updatedCategory)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
<<<<<<< HEAD
                            "Cập nhật thành công!",
=======
                            "Cập nhật danh mục thành công!",
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                            Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(requireView()).popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
<<<<<<< HEAD
                                "Lỗi: " + e.getMessage(),
=======
                                "Lỗi cập nhật: " + e.getMessage(),
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
                                Toast.LENGTH_SHORT).show()
                );
    }

<<<<<<< HEAD
    // ================= ICON INDEX =================
=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    private int getIconIndex(String iconName) {
        for (int i = 0; i < ICON_NAMES.length; i++) {
            if (ICON_NAMES[i].equals(iconName)) {
                return i;
            }
        }
        return -1;
    }
}