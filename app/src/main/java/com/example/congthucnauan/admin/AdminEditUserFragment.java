package com.example.congthucnauan.admin;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.congthucnauan.R;
import com.example.congthucnauan.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminEditUserFragment extends Fragment {

    private static final String ARG_USER = "arg_user";

    // ── Views ──────────────────────────────────────
    private TextInputEditText etFullName, etEmail, etPhone;
    private RadioGroup        rgRole;
    private RadioButton       rbUser, rbAdmin;
    private TextView          tvUid, tvAvatarUrl, tvCreatedAt, tvInitials;
    private View              frameInitials;
    private Button            btnSave, btnCancel, btnDeleteAccount;
    private ImageView imgAvatar;
    private User currentUser;

    // ── Factory ────────────────────────────────────
    public static AdminEditUserFragment newInstance(User user) {
        AdminEditUserFragment fragment = new AdminEditUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_edit_user, container, false);
    }
    private ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadImageToFirebase(uri);
                }
            });
    private void uploadImageToFirebase(Uri uri) {
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("avatars/" + uid + ".jpg");

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            saveAvatarUrl(downloadUri.toString());
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveAvatarUrl(String url) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("avatarUrl")
                .setValue(url)
                .addOnSuccessListener(unused -> {

                    tvAvatarUrl.setText(url);


                    Glide.with(this)
                            .load(url)
                            .into(imgAvatar);

                    Toast.makeText(getContext(), "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lưu lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private void openImagePicker() {
        imagePicker.launch("image/*");
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        etFullName      = view.findViewById(R.id.etFullName);
        etEmail         = view.findViewById(R.id.etEmail);
        etPhone         = view.findViewById(R.id.etPhone);
        rgRole          = view.findViewById(R.id.rgRole);
        rbUser          = view.findViewById(R.id.rbUser);
        rbAdmin         = view.findViewById(R.id.rbAdmin);
        tvUid           = view.findViewById(R.id.tvUid);
        tvAvatarUrl     = view.findViewById(R.id.tvAvatarUrl);
        tvCreatedAt     = view.findViewById(R.id.tvCreatedAt);
        tvInitials      = view.findViewById(R.id.tvInitials);
        frameInitials   = view.findViewById(R.id.frameInitials);
        btnSave         = view.findViewById(R.id.btnSave);
        btnCancel       = view.findViewById(R.id.btnCancel);
        btnDeleteAccount= view.findViewById(R.id.btnDeleteAccount);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Điền dữ liệu
        if (currentUser != null) {
            populateFields(currentUser);
        }
        ImageView btnCamera = view.findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(v -> openImagePicker());

        // Listeners
        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        btnDeleteAccount.setOnClickListener(v -> confirmDelete());
    }

    // ── Điền dữ liệu User vào form ─────────────────
    private void populateFields(User user) {
        // Editable fields
        etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");

        // Role radio
        if ("admin".equalsIgnoreCase(user.getRole())) {
            rbAdmin.setChecked(true);
        } else {
            rbUser.setChecked(true);
        }

        // Read-only info
        tvUid.setText(user.getUid() != null ? user.getUid() : "-");

        String avatarUrl = user.getAvatarUrl();
        tvAvatarUrl.setText((avatarUrl != null && !avatarUrl.isEmpty()) ? avatarUrl : "Chưa có ảnh");

        if (user.getCreatedAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvCreatedAt.setText(sdf.format(new Date(user.getCreatedAt())));
        } else {
            tvCreatedAt.setText("Không rõ");
        }

        // Avatar initials
        String name = user.getFullName();
        if (name == null || name.trim().isEmpty()) {
            String email = user.getEmail();
            name = (email != null && email.contains("@"))
                    ? email.substring(0, email.indexOf('@'))
                    : "?";
        }
        tvInitials.setText(getInitials(name));
        if (frameInitials != null) frameInitials.setVisibility(View.VISIBLE);
    }

    // ── Lưu thay đổi lên Firebase ──────────────────
    private void saveUser() {
        if (currentUser == null) return;

        String fullName = etFullName.getText() != null
                ? etFullName.getText().toString().trim() : "";
        String phone    = etPhone.getText() != null
                ? etPhone.getText().toString().trim() : "";
        String role     = rbAdmin.isChecked() ? "admin" : "user";

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("fullName").setValue(fullName)
                .addOnSuccessListener(u1 ->
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(currentUser.getUid())
                                .child("phone").setValue(phone)
                                .addOnSuccessListener(u2 ->
                                        FirebaseDatabase.getInstance()
                                                .getReference("users")
                                                .child(currentUser.getUid())
                                                .child("role").setValue(role)
                                                .addOnSuccessListener(u3 -> {
                                                    btnSave.setEnabled(true);
                                                    btnSave.setText("Lưu thay đổi");
                                                    Toast.makeText(getContext(),
                                                            "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                    requireActivity().getSupportFragmentManager().popBackStack();
                                                })
                                                .addOnFailureListener(this::handleSaveError)
                                )
                                .addOnFailureListener(this::handleSaveError)
                )
                .addOnFailureListener(this::handleSaveError);
    }

    private void handleSaveError(Exception e) {
        btnSave.setEnabled(true);
        btnSave.setText("Lưu thay đổi");
        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // ── Xóa tài khoản ──────────────────────────────
    private void confirmDelete() {
        if (currentUser == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa tài khoản \"" + currentUser.getFullName() + "\"?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (d, w) ->
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(currentUser.getUid())
                                .removeValue()
                                .addOnSuccessListener(u -> {
                                    Toast.makeText(getContext(),
                                            "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(),
                                                "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ── Helper ─────────────────────────────────────
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}