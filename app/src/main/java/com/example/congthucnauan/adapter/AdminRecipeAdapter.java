package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class AdminRecipeAdapter extends RecyclerView.Adapter<AdminRecipeAdapter.VH> {

    public interface OnDeleteClick {
        void onDelete(Recipe r);
    }

    private List<Recipe> list = new ArrayList<>();
    private final OnDeleteClick deleteListener;

    public AdminRecipeAdapter(List<Recipe> list, OnDeleteClick del) {
        if (list != null) this.list = list;
        this.deleteListener = del;
    }

    // ===== SET DATA =====
    public void setData(List<Recipe> data) {
        if (data == null) return;
        this.list = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_recipe, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe r = list.get(position);

        // ===== NAME =====
        h.tvName.setText(
                r.getName() != null ? r.getName() : "Chưa có tên"
        );

        // ===== DESCRIPTION (steps) =====
        h.tvInfo.setText(
                r.getSteps() != null ? r.getSteps() : "Không có mô tả"
        );

        // ===== IMAGE =====
        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(r.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(h.imgRecipe);
        } else {
            h.imgRecipe.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // ===== DELETE =====
        h.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ===== VIEW HOLDER =====
    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvInfo;
        ImageView imgRecipe;
        ImageButton btnDelete;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvInfo = v.findViewById(R.id.tvInfo);
            imgRecipe = v.findViewById(R.id.imgRecipe);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}