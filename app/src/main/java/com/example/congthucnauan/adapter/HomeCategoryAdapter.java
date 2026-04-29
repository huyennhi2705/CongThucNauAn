package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
=======
import android.widget.ImageView;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.VH> {

<<<<<<< HEAD
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    private List<Category> list;
    private OnItemClickListener listener;
=======
    private List<Category> list;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4

    public HomeCategoryAdapter(List<Category> list) {
        this.list = list;
    }

<<<<<<< HEAD
    public HomeCategoryAdapter(List<Category> list, OnItemClickListener listener) {
        this.list     = list;
        this.listener = listener;
    }

=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    public void setData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
<<<<<<< HEAD
                .inflate(R.layout.item_home_category, parent, false);
=======
                .inflate(R.layout.item_category_home, parent, false);
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Category cat = list.get(pos);

<<<<<<< HEAD
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
=======
        h.txtName.setText(cat.getName());

        // 🔥 LOAD ẢNH TỪ DRAWABLE
        int resId = h.itemView.getContext().getResources()
                .getIdentifier(cat.getIcon(), "drawable",
                        h.itemView.getContext().getPackageName());

        if (resId != 0) {
            h.imgIcon.setImageResource(resId);
        } else {
            h.imgIcon.setImageResource(R.drawable.ic_launcher_foreground);
        }
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    }

    @Override
    public int getItemCount() {
<<<<<<< HEAD
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
=======
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName;

        VH(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        }
    }
}