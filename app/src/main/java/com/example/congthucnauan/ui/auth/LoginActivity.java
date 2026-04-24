package com.example.congthucnauan.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.congthucnauan.MainActivity;
import com.example.congthucnauan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editPassword;
    private MaterialButton buttonLogin;
    private TextView textSignUp, txtForgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private static final String FIREBASE_URL =
            "https://ctna-996dc-default-rtdb.firebaseio.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textSignUp = findViewById(R.id.textSignUp);
        txtForgotPassword =findViewById(R.id.txtForgotPassword);
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra nếu có email/password từ RegisterActivity
        if (getIntent() != null) {
            String emailFromRegister = getIntent().getStringExtra("email");
            String passwordFromRegister = getIntent().getStringExtra("password");
            if (emailFromRegister != null && passwordFromRegister != null) {
                editEmail.setText(emailFromRegister);
                editPassword.setText(passwordFromRegister);
            }
        }
        buttonLogin.setOnClickListener(v -> loginUser());
        textSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (email.isEmpty()) {
            editEmail.setError("Vui lòng nhập email");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}