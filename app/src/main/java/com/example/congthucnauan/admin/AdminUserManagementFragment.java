package com.example.congthucnauan.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.AdminUserAdapter;
import com.example.congthucnauan.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementFragment extends Fragment {

    private static final String DB_URL = "https://ctna-996dc-default-rtdb.firebaseio.com/";

    private RecyclerView       recyclerView;
    private AdminUserAdapter   adapter;
    private DatabaseReference  usersRef;
    private ValueEventListener usersListener;

    private TextView    tvUserCount, tvEmpty;
    private ProgressBar progressBar;
    private EditText    etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        tvUserCount = view.findViewById(R.id.tvUserCount);
        tvEmpty     = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch    = view.findViewById(R.id.etSearch);

        recyclerView = view.findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminUserAdapter(new AdminUserAdapter.OnUserClickListener() {
            @Override public void onClick(User user)  { openEditFragment(user); }
            @Override public void onEdit(User user)   { openEditFragment(user); }
            @Override public void onDelete(User user) { confirmDelete(user); }
        });
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.filter(s.toString());
            }
        });

        loadUsers();
    }

    // ─────────────────────────────────────────────
    //  Load toàn bộ users – đọc từng field thủ công
    //  để tránh lỗi mapping tên field Firebase
    // ─────────────────────────────────────────────
    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        usersRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");

        usersListener = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                List<User> list = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    // ── Đọc từng field thủ công để đảm bảo không bị null ──
                    String uid       = ds.getKey();
                    String email     = getStr(ds, "email");
                    String fullName  = getStr(ds, "fullName");
                    String phone     = getStr(ds, "phone");
                    String avatarUrl = getStr(ds, "avatarUrl");
                    String role      = getStr(ds, "role");
                    long   createdAt = getLong(ds, "createdAt");

                    // Fallback uid: nếu field uid tồn tại trong node thì dùng
                    String uidField = getStr(ds, "uid");
                    if (uidField != null && !uidField.isEmpty()) uid = uidField;

                    // role mặc định
                    if (role == null || role.isEmpty()) role = "user";

                    User user = new User(uid, email, fullName, phone, avatarUrl, role, createdAt);
                    list.add(user);
                }

                adapter.setData(list);
                int count = list.size();
                tvUserCount.setText(count + " người dùng");
                tvEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─────────────────────────────────────────────
    //  Helper đọc String / long an toàn
    // ─────────────────────────────────────────────
    private String getStr(DataSnapshot ds, String key) {
        Object val = ds.child(key).getValue();
        return val != null ? val.toString() : "";
    }

    private long getLong(DataSnapshot ds, String key) {
        Object val = ds.child(key).getValue();
        if (val == null) return 0L;
        try { return Long.parseLong(val.toString()); }
        catch (NumberFormatException e) { return 0L; }
    }

    // ─────────────────────────────────────────────
    //  Điều hướng sang trang sửa
    // ─────────────────────────────────────────────
    private void openEditFragment(User user) {
        AdminEditUserFragment editFragment = AdminEditUserFragment.newInstance(user);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.nav_host, editFragment)
                .addToBackStack(null)
                .commit();
    }

    // ─────────────────────────────────────────────
    //  Xóa user
    // ─────────────────────────────────────────────
    private void confirmDelete(User user) {
        String label = (user.getFullName() != null && !user.getFullName().isEmpty())
                ? user.getFullName() : user.getEmail();

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa tài khoản \"" + label + "\"?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (d, w) ->
                        FirebaseDatabase.getInstance(DB_URL)
                                .getReference("users")
                                .child(user.getUid())
                                .removeValue()
                                .addOnSuccessListener(u ->
                                        Toast.makeText(getContext(),
                                                "Đã xóa tài khoản", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(),
                                                "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (usersRef != null && usersListener != null)
            usersRef.removeEventListener(usersListener);
    }
}