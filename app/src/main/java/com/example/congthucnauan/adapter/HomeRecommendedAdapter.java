        package com.example.congthucnauan.adapter;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.example.congthucnauan.R;
        import com.example.congthucnauan.models.Recipe;
        import com.example.congthucnauan.ui.home.HomeFragment;

        import android.widget.ImageView;
        import java.util.List;
        public class HomeRecommendedAdapter extends RecyclerView.Adapter<HomeRecommendedAdapter.ViewHolder> {

            private List<Recipe> list;
            private OnItemClick listener;


            public interface OnItemClick {
                void onClick(Recipe recipe);
            }


            public HomeRecommendedAdapter(List<Recipe> list, OnItemClick listener) {
                this.list = list;
                this.listener = listener;
            }

            public void setData(List<Recipe> newList) {
                this.list = newList;
                notifyDataSetChanged();
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_recommended, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                Recipe item = list.get(position);

                holder.txtName.setText(item.getName() != null ? item.getName() : "Không có tên");

                String imageUrl = item.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    // Fallback ảnh mặc định nếu không có imageUrl
                    imageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/53/Pho-Beef-Noodles-2008.jpg";
                }

                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)  // ảnh chờ
                            .error(R.drawable.ic_launcher_foreground)       // ảnh lỗi
                            .centerCrop()                                   // ảnh đẹp, không bị méo
                            .into(holder.imgFood);

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onClick(item);
                });
            }
            @Override
            public int getItemCount() {
                return list != null ? list.size() : 0;
            }

            public static class ViewHolder extends RecyclerView.ViewHolder {
                TextView txtName;
                ImageView imgFood;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    txtName = itemView.findViewById(R.id.txtName);
                    imgFood = itemView.findViewById(R.id.imgFood);
                }
            }
        }