package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.VH> {

    private List<Category> list;

    public HomeCategoryAdapter(List<Category> list) {
        this.list = list;
    }

    public void setData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_home, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Category cat = list.get(pos);

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName;

        VH(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}