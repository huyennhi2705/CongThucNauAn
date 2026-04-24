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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryFragment extends Fragment {

    private TextInputEditText edtName, edtDescription;
    private ImageView imgIcon;
    private TextView txtIconLabel;
    private FloatingActionButton btnAdd;
    private MaterialToolbar toolbar;
    private View cardIconPicker;

    private DatabaseReference categoryRef;

    // Icon mặc định
    private String selectedIcon = "monanchinh";

    // Tên icon lưu vào Firebase
    private final String[] ICON_NAMES = {
            "monanchinh",
            "montrangmieng",
            "monannuoc",
            "monnuoc"
    };

    // Tên hiển thị cho người dùng
    private final String[] ICON_LABELS = {
            "Món ăn chính",
            "Món tráng miệng",
            "Món ăn nước",
            "Món nước"
    };

    // Drawable tương ứng
    private final int[] ICON_RES = {
            R.drawable.monanchinh,
            R.drawable.montrangmieng,
            R.drawable.monannuoc,
            R.drawable.monnuoc
    };

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        edtName = view.findViewById(R.id.edtName);
        edtDescription = view.findViewById(R.id.edtDescription);
        imgIcon = view.findViewById(R.id.imgIcon);
        txtIconLabel = view.findViewById(R.id.txtIconLabel);
        btnAdd = view.findViewById(R.id.buttonAdd); // ✅ đúng với XML của bạn
        toolbar = view.findViewById(R.id.toolbarTop);
        cardIconPicker = view.findViewById(R.id.cardIconPicker);

        // Firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        // Hiển thị icon mặc định
        imgIcon.setImageResource(ICON_RES[0]);
        txtIconLabel.setText(ICON_LABELS[0]);

        // Chọn icon
        if (cardIconPicker != null) {
            cardIconPicker.setOnClickListener(v -> showIconPickerDialog());
        }

        // Nút quay lại
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v ->
                    Navigation.findNavController(v).popBackStack()
            );
        }

        // Nút thêm
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> saveCategory());
        } else {
            Toast.makeText(requireContext(),
                    "Không tìm thấy buttonAdd trong fragment_add_category.xml",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showIconPickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn icon cho danh mục")
                .setItems(ICON_LABELS, (dialog, which) -> {
                    selectedIcon = ICON_NAMES[which];
                    imgIcon.setImageResource(ICON_RES[which]);
                    txtIconLabel.setText(ICON_LABELS[which]);
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void saveCategory() {
        String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
        String desc = edtDescription.getText() != null ? edtDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Vui lòng nhập tên danh mục");
            edtName.requestFocus();
            return;
        }

        String newId = categoryRef.push().getKey();

        if (newId == null) {
            Toast.makeText(requireContext(),
                    "Không thể tạo ID danh mục!",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        Category category = new Category(
                newId,
                "Danh mục",
                name,
                desc,
                selectedIcon
        );

        categoryRef.child(newId).setValue(category)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Đã thêm danh mục: " + name,
                            Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(requireView()).popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}