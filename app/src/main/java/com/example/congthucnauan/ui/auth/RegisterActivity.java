package com.example.congthucnauan.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
<<<<<<< HEAD
import android.view.View;
=======
import android.util.Patterns;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.congthucnauan.R;
<<<<<<< HEAD
import com.example.congthucnauan.models.User;
=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
<<<<<<< HEAD
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

=======
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editPassword, editPassword2;
    private MaterialButton buttonSignUp;
    private FirebaseAuth mAuth;
    TextView textSignIn;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
<<<<<<< HEAD

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

=======
        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Ánh xạ view
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textSignIn = findViewById(R.id.textSignIn);
        buttonSignUp.setOnClickListener(v -> registerUser());
        textSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }
    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editPassword2.getText().toString().trim();
        // Kiểm tra trống
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Vui lòng nhập email");
            editEmail.requestFocus();
            return;
        }
<<<<<<< HEAD
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
=======
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }
<<<<<<< HEAD

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

=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
<<<<<<< HEAD
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
=======
            editPassword.setError("Mật khẩu phải >= 6 ký tự");
            editPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editPassword2.setError("Mật khẩu không khớp");
            editPassword2.requestFocus();
            return;
        }
        // Tạo tài khoản trên Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lấy user vừa tạo
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Tạo object user (có thể mở rộng thêm tên, số điện thoại,...)
                            User user = new User(email);

                            // Lưu vào Realtime Database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId)
                                    .setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                            // Quay về LoginActivity để đăng nhập
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Lỗi lưu Database", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Class User để lưu thông tin cơ bản vào Database
    public static class User {
        public String email;

        public User() {
            // Firebase cần constructor mặc định
        }

        public User(String email) {
            this.email = email;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        }
    }
}