package com.example.congthucnauan;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private String currentUserRole;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        currentUserRole = getIntent().getStringExtra("userRole");
        currentUserId   = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserRole == null) currentUserRole = "users";
        if (currentUserId   == null) currentUserId   = "";
        if (currentUserName == null) currentUserName  = "Người dùng";

        // ✅ Lấy NavController an toàn qua NavHostFragment
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_menu);

        if ("admin".equals(currentUserRole)) {

            bottomNav.setVisibility(View.GONE);
            navController.navigate(R.id.adminDashboardFragment);

        } else {

            bottomNav.setVisibility(View.VISIBLE);


            NavigationUI.setupWithNavController(bottomNav, navController);


        }
    }

    public String getCurrentUserRole() { return currentUserRole; }
    public String getCurrentUserId()   { return currentUserId; }
    public String getCurrentUserName() { return currentUserName; }
}