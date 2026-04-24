package com.example.congthucnauan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.congthucnauan.utils.ProvinceHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPlaceFragment extends Fragment {

    // Firebase
    private DatabaseReference refPlace;

    // Helper
    private ProvinceHelper provinceHelper;

    // Views
    private AutoCompleteTextView dropdownProvince, dropdownDistrict;
    private Chip chipAddTag;
    private ChipGroup chipGroupTags;
    private FloatingActionButton buttonSave;
    private TextInputEditText editName, editDescription;

    // Data
    private final List<String> tags = new ArrayList<>();
    private String categoryId = "";

    public AddPlaceFragment() {
        // Required empty public constructor
    }

    private void initFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        refPlace = db.getReference("places");
    }

    // Khởi tạo danh sách tỉnh, thành phố, phường, xã
    private void initProvinceHelper() {
        provinceHelper = new ProvinceHelper(requireContext());
    }

    private void setupDropdowns() {
        List<String> provinces = provinceHelper.getProvinceNames();

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                provinces
        );

        dropdownProvince.setAdapter(provinceAdapter);

        dropdownProvince.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvince = parent.getItemAtPosition(position).toString();
            updateDistrictDropdown(selectedProvince);
            dropdownDistrict.setText("", false);
        });

        dropdownDistrict.setEnabled(false);
    }

    private void updateDistrictDropdown(String province) {
        List<String> districts = provinceHelper.getDistricts(province);
        if (districts.isEmpty()) return;

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                districts
        );

        dropdownDistrict.setAdapter(districtAdapter);
        dropdownDistrict.setEnabled(true);
    }

    // Hàm xử lý cho các thao tác thẻ
    private void setupTagChips() {
        chipAddTag.setOnClickListener(v -> showAddTagDialog());
    }

    private void showAddTagDialog() {
        String[] suggestedTags = {
                "Riverside", "Orchard", "Boat Trip", "Local Food", "Homestay",
                "Photography", "Peaceful", "Family-friendly", "Outdoor", "Cultural"
        };

        List<String> available = new ArrayList<>();
        for (String tag : suggestedTags) {
            if (!tags.contains(tag)) {
                available.add(tag);
            }
        }

        boolean[] checked = new boolean[available.size()];

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose tags")
                .setPositiveButton("Yes", (dialog, which) -> {
                    for (int i = 0; i < checked.length; i++) {
                        if (checked[i]) {
                            addTagChip(available.get(i));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setMultiChoiceItems(
                        available.toArray(new String[0]),
                        checked,
                        (dialog, which, isChecked) -> checked[which] = isChecked
                )
                .show();
    }

    private void addTagChip(String tagName) {
        if (tags.contains(tagName)) return;

        tags.add(tagName);

        Chip chip = new Chip(requireContext());
        chip.setText(tagName);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(v -> {
            chipGroupTags.removeView(chip);
            tags.remove(tagName);
        });

        int addTagIndex = chipGroupTags.indexOfChild(chipAddTag);
        chipGroupTags.addView(chip, addTagIndex);
    }

    // Hàm xử lý sự kiện cho nút lưu
    private void savePlace() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String description = editDescription.getText() != null ? editDescription.getText().toString().trim() : "";
        String province = dropdownProvince.getText().toString().trim();
        String district = dropdownDistrict.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Vui lòng nhập tên địa điểm");
            return;
        }

        if (province.isEmpty()) {
            dropdownProvince.setError("Vui lòng chọn tỉnh/thành");
            return;
        }

        if (district.isEmpty()) {
            dropdownDistrict.setError("Vui lòng chọn quận/huyện");
            return;
        }

        buttonSave.setEnabled(false);

        String key = refPlace.push().getKey();
        if (key == null) {
            buttonSave.setEnabled(true);
            return;
        }

        Map<String, Object> place = new HashMap<>();
        place.put("id", key);
        place.put("categoryId", categoryId);
        place.put("name", name);
        place.put("description", description);
        place.put("province", province);
        place.put("district", district);
        place.put("tags", tags);
        place.put("likes", 0);

        refPlace.child(key).setValue(place)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Place added successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    buttonSave.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        editName = view.findViewById(R.id.edtName);
        editDescription = view.findViewById(R.id.editDescription);
        dropdownProvince = view.findViewById(R.id.dropdownProvince);
        dropdownDistrict = view.findViewById(R.id.dropdownDistrict);
        chipAddTag = view.findViewById(R.id.chipAddTag);
        chipGroupTags = view.findViewById(R.id.chipGroupTags);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Lấy categoryId nếu có truyền sang
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId", "");
        }

        initFirebase();
        initProvinceHelper();
        setupDropdowns();
        setupTagChips();

        buttonSave.setOnClickListener(v -> savePlace());
    }
}