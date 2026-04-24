package com.example.congthucnauan.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.congthucnauan.R;
import com.example.congthucnauan.adapter.HomeCategoryAdapter;
import com.example.congthucnauan.models.Category;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseReference categoryRef;
    private ValueEventListener categoryListener;
    private HomeCategoryAdapter adapter;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ===== NAVIGATION =====

        view.findViewById(R.id.textViewCategory).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_category)
        );

        // ===== RECYCLER CATEGORY =====
        RecyclerView recyclerCategory = view.findViewById(R.id.recyclerCategory);
        recyclerCategory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        adapter = new HomeCategoryAdapter(new ArrayList<>());
        recyclerCategory.setAdapter(adapter);

        // ===== FIREBASE =====
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        categoryListener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> list = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category cat = data.getValue(Category.class);
                    if (cat != null) list.add(cat);
                }
                adapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }
}