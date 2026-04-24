package com.example.congthucnauan.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editFullName, editEmail, editPhone, editPassword, editConfirmPassword;
    private MaterialButton buttonRegister;
    private TextView textLogin;
    private View progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Firebase Realtime Database URL
    private static final String DB_URL = "https://ctna-996dc-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();

        // Bind views
        editFullName        = findViewById(R.id.editFullName);
        editEmail           = findViewById(R.id.editEmail);
        editPhone           = findViewById(R.id.editPhone);
        editPassword        = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        buttonRegister      = findViewById(R.id.buttonRegister);
        textLogin           = findViewById(R.id.textLogin);
        progressBar         = findViewById(R.id.progressBar);

        buttonRegister.setOnClickListener(v -> registerUser());
        textLogin.setOnClickListener(v -> finish()); // quay về LoginActivity
    }

    // ─────────────────────────────────────────────
    //  Validate & Đăng ký
    // ─────────────────────────────────────────────
    private void registerUser() {
        String fullName       = editFullName.getText().toString().trim();
        String email          = editEmail.getText().toString().trim();
        String phone          = editPhone.getText().toString().trim();
        String password       = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // --- Validation ---
        if (TextUtils.isEmpty(fullName)) {
            editFullName.setError("Vui lòng nhập họ và tên");
            editFullName.requestFocus();
            return;
        }
        if (fullName.length() < 2) {
            editFullName.setError("Họ và tên phải ít nhất 2 ký tự");
            editFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Vui lòng nhập email");
            editEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Vui lòng nhập số điện thoại");
            editPhone.requestFocus();
            return;
        }
        if (!phone.matches("^(0|\\+84)[0-9]{9}$")) {
            editPhone.setError("Số điện thoại không hợp lệ (VD: 0901234567)");
            editPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Mật khẩu phải ít nhất 6 ký tự");
            editPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            editConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            editConfirmPassword.requestFocus();
            return;
        }


        setLoading(true);


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        setLoading(false);
                        Toast.makeText(this, "Đăng ký thất bại, thử lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = firebaseUser.getUid();
                    long createdAt = System.currentTimeMillis();

                    // --- Tạo object User ---
                    User newUser = new User(
                            uid,
                            email,
                            fullName,
                            phone,
                            "",        // avatarUrl: trống, cập nhật sau
                            "user",    // role mặc định
                            createdAt
                    );

                    // --- Lưu vào Firebase Realtime Database ---
                    mDatabase.child("users").child(uid)
                            .setValue(newUser)
                            .addOnSuccessListener(unused -> {
                                setLoading(false);
                                Toast.makeText(this,
                                        "Đăng ký thành công! Chào " + fullName,
                                        Toast.LENGTH_LONG).show();

                                // Chuyển về LoginActivity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                // Auth đã tạo nhưng DB lỗi → xóa auth để tránh orphan account
                                firebaseUser.delete();
                                Toast.makeText(this,
                                        "Lỗi lưu dữ liệu: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    String errorMsg = "Đăng ký thất bại";

                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("email address is already in use")) {
                            errorMsg = "Email này đã được đăng ký";
                            editEmail.setError(errorMsg);
                            editEmail.requestFocus();
                        } else if (e.getMessage().contains("network")) {
                            errorMsg = "Kiểm tra kết nối Internet";
                        } else if (e.getMessage().contains("badly formatted")) {
                            errorMsg = "Email không đúng định dạng";
                            editEmail.setError(errorMsg);
                            editEmail.requestFocus();
                        }
                    }

                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                });
    }

    // ─────────────────────────────────────────────
    //  Helper: toggle loading state
    // ─────────────────────────────────────────────
    private void setLoading(boolean isLoading) {
        buttonRegister.setEnabled(!isLoading);
        buttonRegister.setText(isLoading ? "Đang xử lý..." : "Đăng ký");
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}