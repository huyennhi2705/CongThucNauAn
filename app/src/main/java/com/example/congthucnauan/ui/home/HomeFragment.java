package com.example.congthucnauan.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;

import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.HomeMoreTripAdapter;
import com.example.congthucnauan.adapter.HomeRecommendedAdapter;
import com.example.congthucnauan.models.Category;
import com.example.congthucnauan.models.Recipe;
import com.google.firebase.database.*;

import java.util.*;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Views
    private RecyclerView recyclerMoreTrip, recyclerRecommended;
    private TextView textViewMoreTrip, textViewRecommended;

    // Adapter
    private HomeMoreTripAdapter moreAdapter;
    private HomeRecommendedAdapter recommendedAdapter;

    // Firebase
    private DatabaseReference categoryRef, recipeRef;
    private ValueEventListener categoryListener, recipeListener;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // ===== INIT VIEW =====
            recyclerMoreTrip = view.findViewById(R.id.recyclerMoreTrip);
            recyclerRecommended = view.findViewById(R.id.recyclerRecommended);
            textViewMoreTrip = view.findViewById(R.id.textViewMoreTrip);
            textViewRecommended = view.findViewById(R.id.textViewRecommended);

            // ===== SETUP RECYCLER =====
            recyclerMoreTrip.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );

            recyclerRecommended.setLayoutManager(
                    new LinearLayoutManager(requireContext())
            );

            // ===== FIREBASE =====
            categoryRef = FirebaseDatabase.getInstance().getReference("categories");
            recipeRef = FirebaseDatabase.getInstance().getReference("recipes");

            // ===== MORE TRIP (CATEGORY) =====
            moreAdapter = new HomeMoreTripAdapter(
                    new ArrayList<Category>(),
                    category -> {
                        Toast.makeText(requireContext(),
                                "Chọn: " + category.getName(),
                                Toast.LENGTH_SHORT).show();

                        // 👉 điều hướng
                        Bundle b = new Bundle();
                        b.putString("categoryId", category.getId());

                        Navigation.findNavController(view)
                                .navigate(R.id.action_home_to_category, b);
                    }
            );

            recyclerMoreTrip.setAdapter(moreAdapter);

            // ===== CLICK MORE =====
            if (textViewMoreTrip != null) {
                textViewMoreTrip.setOnClickListener(v ->
                        Navigation.findNavController(v)
                                .navigate(R.id.action_home_to_category)
                );
            }

            // ===== RECOMMENDED =====
            recommendedAdapter = new HomeRecommendedAdapter(
                    new ArrayList<Recipe>(),   // ✅ FIX TYPE
                    recipe -> {
                        if (recipe == null || recipe.getId() == null) {
                            Toast.makeText(requireContext(),
                                    "Recipe lỗi",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Bundle args = new Bundle();
                        args.putString("recipeId", recipe.getId());

                        Navigation.findNavController(view) // ✅ FIX NAV
                                .navigate(R.id.action_recommended_to_detail, args);
                    }
            );

            recyclerRecommended.setAdapter(recommendedAdapter);

            // ===== CLICK HEADER =====
            if (textViewRecommended != null) {
                textViewRecommended.setOnClickListener(v ->
                        Navigation.findNavController(v)
                                .navigate(R.id.action_home_to_recommended)
                );
            }

            // ===== LOAD DATA =====
            loadCategories();
            loadRecipes();

        } catch (Exception e) {
            Log.e(TAG, "Init error: " + e.getMessage());
        }
    }

    // ===== LOAD CATEGORY =====
    private void loadCategories() {
        categoryListener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Category c = data.getValue(Category.class);
                    if (c != null) {
                        c.setId(data.getKey());
                        list.add(c);
                    }
                }

                moreAdapter.setData(list); // ✅ đúng
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Category error: " + error.getMessage());
            }
        });
    }

    // ===== LOAD RECIPES =====
    private void loadRecipes() {
        recipeListener = recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Recipe r = data.getValue(Recipe.class);
                    if (r != null) {
                        r.setId(data.getKey());
                        list.add(r);

                        if (list.size() >= 5) break; // limit
                    }
                }

                recommendedAdapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Recipe error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }

        if (recipeRef != null && recipeListener != null) {
            recipeRef.removeEventListener(recipeListener);
        }
    }
}