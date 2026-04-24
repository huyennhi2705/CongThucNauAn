package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRvAdapter extends RecyclerView.Adapter<CategoryRvAdapter.VH> {

    public interface OnCategoryListener {
        void onItemClick(Category category);
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    private final int layoutRes;
    private final OnCategoryListener listener;
    private List<Category> list = new ArrayList<>();

    public CategoryRvAdapter(int layoutRes, OnCategoryListener listener) {
        this.layoutRes = layoutRes;
        this.listener = listener;
    }

    public void setCategories(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category category = list.get(position);

        holder.textName.setText(category.getName() != null ? category.getName() : "");
        holder.textDescription.setText(category.getDescription() != null ? category.getDescription() : "");
        holder.textType.setText(category.getType() != null ? category.getType() : "");

        holder.imageIcon.setImageResource(getIconRes(category.getIcon(), holder.imageIcon));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(category);
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add(0, 1, 0, "✏️ Sửa");
            popupMenu.getMenu().add(0, 2, 1, "🗑️ Xoá");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener == null) return false;

                if (item.getItemId() == 1) {
                    listener.onEditClick(category);
                    return true;
                } else if (item.getItemId() == 2) {
                    listener.onDeleteClick(category);
                    return true;
                }

                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private int getIconRes(String iconName, ImageView imageView) {
        if (iconName == null || iconName.trim().isEmpty()) {
            return R.drawable.ic_launcher_foreground;
        }

        int resId = imageView.getContext().getResources()
                .getIdentifier(iconName, "drawable",
                        imageView.getContext().getPackageName());

        return resId != 0 ? resId : R.drawable.ic_launcher_foreground;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textName, textDescription, textType;
        ImageView imageIcon;
        ImageButton btnMore;

        public VH(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textDescription = itemView.findViewById(R.id.textDescription);
            textType = itemView.findViewById(R.id.textType);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}