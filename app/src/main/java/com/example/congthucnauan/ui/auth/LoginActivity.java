package com.example.congthucnauan.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.congthucnauan.MainActivity;
import com.example.congthucnauan.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPassword;
    private MaterialButton buttonLogin;
    private TextView textSignUp, txtForgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textSignUp = findViewById(R.id.textSignUp);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // Set click listeners
        buttonLogin.setOnClickListener(v -> loginUser());
        textSignUp.setOnClickListener(v -> openRegisterActivity());
        txtForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            editEmail.setError("Vui lòng nhập email");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("Mật khẩu phải ít nhất 6 ký tự");
            editPassword.requestFocus();
            return;
        }

        // Đăng nhập với Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    checkUserRole(userId);
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Đăng nhập thất bại";

                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("password")) {
                            errorMsg = "Mật khẩu không đúng";
                        } else if (e.getMessage().contains("no user") ||
                                e.getMessage().contains("There is no user")) {
                            errorMsg = "Email chưa được đăng ký";
                        } else if (e.getMessage().contains("network")) {
                            errorMsg = "Kiểm tra kết nối Internet";
                        }
                    }

                    Toast.makeText(LoginActivity.this, errorMsg,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserRole(String userId) {
        // Kiểm tra role của user từ Firebase Realtime Database
        mDatabase.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String role = dataSnapshot.child("role").getValue(String.class);
                            String name = dataSnapshot.child("name").getValue(String.class);

                            if (role == null || role.isEmpty()) {
                                role = "users";
                            }

                            if ("admin".equals(role)) {
                                // Nếu là admin
                                navigateToMainActivity(userId, role, name);
                            } else {
                                // Nếu là user thường
                                navigateToMainActivity(userId, role, name);
                            }
                        } else {
                            // User data not found in database
                            Toast.makeText(LoginActivity.this,
                                    "Dữ liệu người dùng không tồn tại. Vui lòng liên hệ hỗ trợ.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this,
                                "Lỗi cơ sở dữ liệu: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword() {
        String email = editEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editEmail.setError("Vui lòng nhập email để đặt lại mật khẩu");
            editEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Toast.makeText(LoginActivity.this,
                                "Email hướng dẫn đặt lại mật khẩu đã được gửi",
                                Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e -> {
                    String errorMsg = "Không thể gửi email";

                    if (e.getMessage() != null &&
                            e.getMessage().contains("no user")) {
                        errorMsg = "Email này chưa được đăng ký";
                    }

                    Toast.makeText(LoginActivity.this, errorMsg,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainActivity(String userId, String role, String name) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userRole", role);
        intent.putExtra("userName", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}