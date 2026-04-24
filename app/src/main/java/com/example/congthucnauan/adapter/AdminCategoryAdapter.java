package com.example.congthucnauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;

import java.util.List;
import java.util.Map;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.VH> {

    public interface OnItemAction {
        void onAction(int position);
    }

    private final List<Map<String, String>> list;
    private final OnItemAction listener;

    public AdminCategoryAdapter(List<Map<String, String>> list,
                                OnItemAction listener) {
        this.list = list;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvCount;
        ImageButton btnEdit, btnDelete;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvCount = v.findViewById(R.id.tvRecipeCount);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        Map<String, String> item = list.get(pos);

        h.tvName.setText(item.get("name"));
        h.tvCount.setText(item.get("count"));

        h.btnDelete.setOnClickListener(v -> listener.onAction(pos));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}