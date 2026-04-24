package com.example.congthucnauan.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.HomeRecommendedAdapter;
import com.example.congthucnauan.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RecommendedFragment extends Fragment {

    private HomeRecommendedAdapter adapter;
    private DatabaseReference recipeRef;

    public RecommendedFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nút back
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerAllFood);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new HomeRecommendedAdapter(new ArrayList<>(), recipe -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("recipes", recipe);
            NavController nav = Navigation.findNavController(view);
            nav.navigate(R.id.action_category_to_addCategory, bundle);
        });

        recyclerView.setAdapter(adapter);

        // Kết nối Realtime Database
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes");

        loadRecipesFromRealtimeDB();
    }

    private void loadRecipesFromRealtimeDB() {
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Recipe recipe = data.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setId(data.getKey());        // Quan trọng: set ID từ key
                        list.add(recipe);
                    }
                }

                adapter.setData(list);

                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "Không có công thức nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}