package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    private List<Category> list;
    private OnItemClickListener listener;

    public HomeCategoryAdapter(List<Category> list) {
        this.list = list;
    }

    public HomeCategoryAdapter(List<Category> list, OnItemClickListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    public void setData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_category, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Category cat = list.get(pos);

        // ── Emoji icon từ iconName ──
        if (h.tvEmoji != null) {
            h.tvEmoji.setText(getEmojiForIcon(cat.getIcon()));
        }

        // ── Tên danh mục ──
        if (h.tvName != null) {
            h.tvName.setText(cat.getName() != null ? cat.getName() : "");
        }

        // ── Click listener ──
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(cat);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    /**
     * Map iconName (lưu trong Firebase) → emoji hiển thị.
     * Danh sách phải khớp với ICON_DRAWABLE[] trong AddCategoryFragment.
     */
    public static String getEmojiForIcon(String iconName) {
        if (iconName == null || iconName.isEmpty()) return "🍽️";
        switch (iconName) {
            case "ic_food_main":      return "🍚";
            case "ic_food_salad":     return "🥗";
            case "ic_food_dessert":   return "🍰";
            case "ic_drinks":         return "🥤";
            case "ic_dish_of_water":  return "🍜";
            case "ic_snacks":         return "🍟";
            case "ic_fried":          return "🍳";
            case "ic_hotpot":         return "🍲";
            // Tương thích với tên cũ
            case "ic_food_drink":     return "🥤";
            case "ic_food_breakfast": return "🥐";
            case "ic_food_soup":      return "🍜";
            case "ic_food_grill":     return "🥩";
            case "ic_food_rice":      return "🍚";
            case "ic_food_snack":     return "🥗";
            default:                  return "🍽️";
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmoji;
        TextView tvName;

        VH(View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvName  = itemView.findViewById(R.id.tvName);
        }
    }
}