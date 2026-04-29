package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
=======
import android.widget.ImageButton;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
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
<<<<<<< HEAD
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

=======
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
<<<<<<< HEAD
        // ✅ Kiểm tra position hợp lệ
        if (position < 0 || position >= list.size()) {
            return;
        }

        Category category = list.get(position);

        // ✅ Kiểm tra category không null
        if (category == null) {
            return;
        }

        // ✅ Kiểm tra views trước khi set
        if (holder.textName != null) {
            holder.textName.setText(category.getName() != null ? category.getName() : "");
        }

        if (holder.textDescription != null) {
            holder.textDescription.setText(category.getDescription() != null ? category.getDescription() : "");
        }

        if (holder.imageIcon != null) {
            holder.imageIcon.setImageResource(getIconRes(category.getIcon(), holder.imageIcon));
        }

        // ✅ Set click listeners với null check
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && category != null) {
                listener.onItemClick(category);
            }
        });

        if (holder.btnMore != null) {
            holder.btnMore.setOnClickListener(v -> {
                if (category == null) return;

                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenu().add(0, 1, 0, "✏️ Sửa");
                popupMenu.getMenu().add(0, 2, 1, "🗑️ Xoá");

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (listener == null) return false;

                    if (item.getItemId() == 1) {
                        listener.onEditClick(category);
                    } else if (item.getItemId() == 2) {
                        listener.onDeleteClick(category);
                    }
                    return true;
                });

                popupMenu.show();
            });
        }
=======
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
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private int getIconRes(String iconName, ImageView imageView) {
<<<<<<< HEAD
        if (iconName == null || iconName.isEmpty()) {
            return R.drawable.ic_launcher_foreground;
        }

        try {
            int resId = imageView.getContext().getResources()
                    .getIdentifier(iconName, "drawable",
                            imageView.getContext().getPackageName());
            return resId != 0 ? resId : R.drawable.ic_launcher_foreground;
        } catch (Exception e) {
            return R.drawable.ic_launcher_foreground;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDescription;
        ImageView imageIcon;
        Button btnMore;
=======
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
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4

        public VH(@NonNull View itemView) {
            super(itemView);

<<<<<<< HEAD
            // ✅ Tìm views một cách an toàn
            textName = itemView.findViewById(R.id.txtName);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            btnMore = itemView.findViewById(R.id.btnMore);

            // ✅ Log nếu không tìm thấy views (debug)
            if (textName == null) {
                android.util.Log.w("CategoryRvAdapter", "txtName view not found");
            }
            if (textDescription == null) {
                android.util.Log.w("CategoryRvAdapter", "textDescription view not found");
            }
            if (imageIcon == null) {
                android.util.Log.w("CategoryRvAdapter", "imageIcon view not found");
            }
            if (btnMore == null) {
                android.util.Log.w("CategoryRvAdapter", "btnMore view not found");
            }
=======
            textName = itemView.findViewById(R.id.textName);
            textDescription = itemView.findViewById(R.id.textDescription);
            textType = itemView.findViewById(R.id.textType);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            btnMore = itemView.findViewById(R.id.btnMore);
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        }
    }
}