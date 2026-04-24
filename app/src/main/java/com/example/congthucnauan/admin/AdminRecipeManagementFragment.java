package com.example.congthucnauan.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.AdminRecipeAdapter;
import com.example.congthucnauan.models.Recipe;
import com.google.firebase.database.*;

import java.util.*;

public class AdminRecipeManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminRecipeAdapter adapter;
    private DatabaseReference recipeRef;
    private ValueEventListener listener;

    private String categoryId, categoryName;
    private TextView tvTitle, tvTotal, tvEmpty;
    private ProgressBar progressBar;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_recipe_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            categoryId   = args.getString("categoryId", "");
            categoryName = args.getString("categoryName", "Công thức");
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        tvTitle     = view.findViewById(R.id.tvTitle);
        tvTotal     = view.findViewById(R.id.tvTotal);
        tvEmpty     = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        tvTitle.setText("Công thức: " + categoryName);

        recyclerView = view.findViewById(R.id.recyclerRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminRecipeAdapter(new ArrayList<>(), this::confirmDeleteRecipe);
        recyclerView.setAdapter(adapter);

        loadRecipes();
    }

    private void loadRecipes() {
        progressBar.setVisibility(View.VISIBLE);
        // Query recipes by category
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes");

        Query query = categoryId.isEmpty()
                ? recipeRef
                : recipeRef.orderByChild("categoryId").equalTo(categoryId);

        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                List<Recipe> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Recipe r = ds.getValue(Recipe.class);
                    if (r != null) {
                        r.setId(ds.getKey());
                        list.add(r);
                    }
                }
                adapter.setData(list);
                tvTotal.setText(list.size() + " công thức");
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),
                        "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteRecipe(Recipe recipe) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa công thức")
                .setMessage("Xóa \"" + recipe.getName() + "\"?")
                .setPositiveButton("Xóa", (d, w) ->
                        FirebaseDatabase.getInstance().getReference("recipes")
                                .child(recipe.getId()).removeValue()
                                .addOnSuccessListener(u ->
                                        Toast.makeText(getContext(),
                                                "Đã xóa công thức", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recipeRef != null && listener != null)
            recipeRef.removeEventListener(listener);
    }
}
