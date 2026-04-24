package com.example.congthucnauan.recommend;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Recipe;
import com.google.firebase.database.*;

public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView img  = view.findViewById(R.id.imgDetail);
        TextView name  = view.findViewById(R.id.txtName);
        TextView step  = view.findViewById(R.id.txtStep);
        View btnBack = view.findViewById(R.id.btnBack);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack()
        );

        Bundle bundle = getArguments();
        if (bundle == null) {
            Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = bundle.getString("id");
        if (id == null) {
            Toast.makeText(getContext(), "ID null", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("recipes")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        if (recipe != null && isAdded()) {
                            name.setText(recipe.getName());
                            step.setText(recipe.getSteps());
                            Glide.with(requireContext())
                                    .load(recipe.getImageUrl())
                                    .into(img);
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}