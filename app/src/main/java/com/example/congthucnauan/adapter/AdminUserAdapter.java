package com.example.congthucnauan.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onClick(User user);
        void onEdit(User user);
        void onDelete(User user);
    }

    private List<User> fullList     = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();
    private final OnUserClickListener listener;

    // Màu nền avatar xoay vòng
    private static final int[] AVATAR_COLORS = {
            0xFFE8631A, 0xFF1976D2, 0xFF388E3C,
            0xFF7B1FA2, 0xFFF57C00, 0xFF0097A7,
            0xFFE53935, 0xFF00897B
    };

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());

    public AdminUserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    // ── Data ──────────────────────────────────────
    public void setData(List<User> users) {
        fullList     = new ArrayList<>(users);
        filteredList = new ArrayList<>(users);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String q = query.toLowerCase().trim();
            for (User u : fullList) {
                String name  = safe(u.getFullName()).toLowerCase();
                String email = safe(u.getEmail()).toLowerCase();
                String phone = safe(u.getPhone());
                String uid   = safe(u.getUid());
                if (name.contains(q) || email.contains(q)
                        || phone.contains(q) || uid.contains(q)) {
                    filteredList.add(u);
                }
            }
        }
        notifyDataSetChanged();
    }

    // ── Inflate ───────────────────────────────────
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(v);
    }

    // ── Bind – hiển thị TOÀN BỘ thông tin trừ mật khẩu ──
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder h, int position) {
        User user = filteredList.get(position);

        // ── Tên hiển thị ─────────────────────────
        String fullName = safe(user.getFullName());
        String displayName;
        if (!fullName.isEmpty()) {
            displayName = fullName;
        } else {
            // Fallback: lấy phần trước @ của email
            String em = safe(user.getEmail());
            displayName = em.contains("@") ? em.substring(0, em.indexOf('@')) : "Người dùng";
        }
        h.tvName.setText(displayName);

        // ── Email ─────────────────────────────────
        String email = safe(user.getEmail());
        h.tvEmail.setText(email.isEmpty() ? "Chưa có email" : email);

        // ── Số điện thoại ────────────────────────
        String phone = safe(user.getPhone());
        if (phone.isEmpty()) {
            h.tvPhone.setText("Chưa cập nhật");
            h.tvPhone.setTextColor(Color.parseColor("#BBBBBB"));
        } else {
            h.tvPhone.setText(phone);
            h.tvPhone.setTextColor(Color.parseColor("#2D2D2D"));
        }

        // ── Ngày tạo tài khoản ───────────────────
        long createdAt = user.getCreatedAt();
        if (createdAt > 0) {
            h.tvCreatedAt.setText(sdf.format(new Date(createdAt)));
            h.tvCreatedAt.setTextColor(Color.parseColor("#2D2D2D"));
        } else {
            h.tvCreatedAt.setText("Không rõ");
            h.tvCreatedAt.setTextColor(Color.parseColor("#BBBBBB"));
        }

        // ── UID ───────────────────────────────────
        String uid = safe(user.getUid());
        h.tvUid.setText(uid.isEmpty() ? "—" : uid);

        // ── Avatar URL ───────────────────────────
        String avatarUrl = safe(user.getAvatarUrl());
        if (avatarUrl.isEmpty()) {
            h.tvAvatarUrl.setText("Chưa có ảnh");
            h.tvAvatarUrl.setTextColor(Color.parseColor("#BBBBBB"));
        } else {
            h.tvAvatarUrl.setText(avatarUrl);
            h.tvAvatarUrl.setTextColor(Color.parseColor("#1565C0"));
        }

        // ── Avatar circle – initials + màu ────────
        h.tvInitials.setText(getInitials(displayName));
        h.frameAvatar.setBackgroundColor(AVATAR_COLORS[position % AVATAR_COLORS.length]);

        // ── Role badge ────────────────────────────
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        h.tvRole.setText(isAdmin ? "Admin" : "User");
        h.tvRole.setBackgroundResource(isAdmin
                ? R.drawable.badge_admin
                : R.drawable.badge_user);
        h.tvRole.setTextColor(isAdmin
                ? Color.WHITE
                : Color.parseColor("#E8631A"));

        // ── Click listeners ───────────────────────
        h.itemView.setOnClickListener(v -> listener.onClick(user));
        h.btnEdit.setOnClickListener(v   -> listener.onEdit(user));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    // ── Helpers ───────────────────────────────────
    private String safe(String s) { return s != null ? s : ""; }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1)
                + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    // ── ViewHolder ────────────────────────────────
    static class UserViewHolder extends RecyclerView.ViewHolder {
        View         frameAvatar;
        TextView     tvInitials, tvName, tvEmail, tvPhone;
        TextView     tvCreatedAt, tvUid, tvAvatarUrl, tvRole;
        LinearLayout btnEdit, btnDelete;

        UserViewHolder(@NonNull View v) {
            super(v);
            frameAvatar = v.findViewById(R.id.frameAvatar);
            tvInitials  = v.findViewById(R.id.tvInitials);
            tvName      = v.findViewById(R.id.tvName);
            tvEmail     = v.findViewById(R.id.tvEmail);
            tvPhone     = v.findViewById(R.id.tvPhone);
            tvCreatedAt = v.findViewById(R.id.tvCreatedAt);
            tvUid       = v.findViewById(R.id.tvUid);
            tvAvatarUrl = v.findViewById(R.id.tvAvatarUrl);
            tvRole      = v.findViewById(R.id.tvRole);
            btnEdit     = v.findViewById(R.id.btnEdit);
            btnDelete   = v.findViewById(R.id.btnDelete);
        }
    }
}