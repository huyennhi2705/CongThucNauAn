package com.example.congthucnauan.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.congthucnauan.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editPassword, editPassword2;
    private MaterialButton buttonSignUp;
    private FirebaseAuth mAuth;
    TextView textSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Vui lòng nhập email");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
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
        }
    }
}