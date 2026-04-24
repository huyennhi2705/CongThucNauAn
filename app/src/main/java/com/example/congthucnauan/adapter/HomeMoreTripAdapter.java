package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;

import java.util.List;

public class HomeMoreTripAdapter
        extends RecyclerView.Adapter<HomeMoreTripAdapter.VH> {

    public interface OnCategoryClick {
        void onClick(Category category);
    }

    // Màu nền xoay vòng
    private static final int[] CARD_COLORS = {
            0xFF1565C0, // xanh đậm
            0xFF2E7D32, // xanh lá
            0xFFAD1457, // hồng đậm
            0xFFE65100, // cam
            0xFF4527A0, // tím
            0xFF00838F  // cyan
    };

    private List<Category> list;
    private final OnCategoryClick listener;

    public HomeMoreTripAdapter(List<Category> list, OnCategoryClick listener) {
        this.list     = list;
        this.listener = listener;
    }

    public void setData(List<Category> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_more_trip_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        if (list == null || list.isEmpty()) return;
        Category cat = list.get(pos);

        // ── Màu nền ──
        h.layoutBg.setBackgroundColor(CARD_COLORS[pos % CARD_COLORS.length]);

        // ✅ Dùng emoji giống HomeCategoryAdapter
        h.tvEmoji.setText(HomeCategoryAdapter.getEmojiForIcon(cat.getIcon()));

        // ── Tên danh mục ──
        h.tvName.setText(cat.getName() != null ? cat.getName() : "");

        // ── Mô tả ngắn ──
        String desc = cat.getDescription();
        if (desc != null && !desc.isEmpty()) {
            h.tvDesc.setText(desc.length() > 45 ? desc.substring(0, 45) + "..." : desc);
            h.tvDesc.setVisibility(View.VISIBLE);
        } else {
            h.tvDesc.setVisibility(View.GONE);
        }

        // ── Click ──
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(cat);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout layoutBg;
        TextView tvEmoji, tvName, tvDesc;

        VH(@NonNull View v) {
            super(v);
            layoutBg = v.findViewById(R.id.layoutBg);
            tvEmoji  = v.findViewById(R.id.tvEmoji);
            tvName   = v.findViewById(R.id.tvCategoryName);
            tvDesc   = v.findViewById(R.id.tvRecipeCount);
        }
    }
}