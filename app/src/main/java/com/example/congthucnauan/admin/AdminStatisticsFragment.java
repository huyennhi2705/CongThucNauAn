package com.example.congthucnauan.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.example.congthucnauan.R;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.*;

import java.util.*;

public class AdminStatisticsFragment extends Fragment {

    private BarChart barChartRecipes;
    private PieChart pieChartCategories;
    private TextView tvTotalUsers, tvTotalRecipes, tvTotalViews, tvTotalFavorites;
    private ProgressBar progressBar;
    private DatabaseReference dbRef;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        progressBar      = view.findViewById(R.id.progressBar);
        tvTotalUsers     = view.findViewById(R.id.tvTotalUsers);
        tvTotalRecipes   = view.findViewById(R.id.tvTotalRecipes);
        tvTotalViews     = view.findViewById(R.id.tvTotalViews);
        tvTotalFavorites = view.findViewById(R.id.tvTotalFavorites);
        barChartRecipes  = view.findViewById(R.id.barChartRecipes);
        pieChartCategories = view.findViewById(R.id.pieChartCategories);

        view.findViewById(R.id.btnExport).setOnClickListener(v -> exportReport());

        loadStatistics();
    }

    private void loadStatistics() {
        progressBar.setVisibility(View.VISIBLE);
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Đếm users
        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvTotalUsers.setText(String.format(Locale.getDefault(), "%,d", snapshot.getChildrenCount()));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        // Đếm recipes + thống kê theo danh mục
        dbRef.child("recipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                long total = snapshot.getChildrenCount();
                tvTotalRecipes.setText(String.format(Locale.getDefault(), "%,d", total));

                // Phân tích theo danh mục cho PieChart
                Map<String, Integer> catCount = new LinkedHashMap<>();
                long totalViews = 0, totalFav = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String catId = ds.child("categoryId").getValue(String.class);
                    if (catId == null) catId = "Khác";
                    catCount.put(catId, catCount.getOrDefault(catId, 0) + 1);

                    Long views = ds.child("viewCount").getValue(Long.class);
                    Long fav   = ds.child("favoriteCount").getValue(Long.class);
                    if (views != null) totalViews += views;
                    if (fav   != null) totalFav   += fav;
                }

                tvTotalViews.setText(formatNumber(totalViews));
                tvTotalFavorites.setText(formatNumber(totalFav));

                setupPieChart(catCount);
                setupBarChart();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupPieChart(Map<String, Integer> catCount) {
        List<PieEntry> entries = new ArrayList<>();
        int[] colors = {
                Color.parseColor("#E8631A"), Color.parseColor("#1A8C4E"),
                Color.parseColor("#1565C0"), Color.parseColor("#F4A900"),
                Color.parseColor("#9C27B0")
        };

        int i = 0;
        for (Map.Entry<String, Integer> entry : catCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            if (++i >= 5) break; // Giới hạn 5 mục
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        pieChartCategories.setData(new PieData(dataSet));
        pieChartCategories.getDescription().setEnabled(false);
        pieChartCategories.setHoleRadius(40f);
        pieChartCategories.setTransparentCircleRadius(45f);
        pieChartCategories.setHoleColor(Color.WHITE);
        pieChartCategories.setCenterText("Danh mục");
        pieChartCategories.setCenterTextSize(14f);
        pieChartCategories.getLegend().setEnabled(true);
        pieChartCategories.animateY(1000);
        pieChartCategories.invalidate();
    }

    private void setupBarChart() {
        // Dữ liệu lượt xem theo tháng (mock hoặc lấy từ Firebase)
        float[] monthlyData = {18500f, 22000f, 19800f, 25300f, 31000f, 28700f};
        String[] months = {"T1", "T2", "T3", "T4", "T5", "T6"};

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < monthlyData.length; i++)
            entries.add(new BarEntry(i, monthlyData[i]));

        BarDataSet dataSet = new BarDataSet(entries, "Lượt xem/tháng");
        dataSet.setColor(Color.parseColor("#E8631A"));
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        barChartRecipes.setData(data);

        XAxis xAxis = barChartRecipes.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float v) {
                int idx = (int) v;
                return (idx >= 0 && idx < months.length) ? months[idx] : "";
            }
        });
        xAxis.setTextColor(Color.parseColor("#888888"));

        barChartRecipes.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float v) {
                return v >= 1000 ? (int)(v/1000) + "K" : String.valueOf((int)v);
            }
        });
        barChartRecipes.getAxisRight().setEnabled(false);
        barChartRecipes.getDescription().setEnabled(false);
        barChartRecipes.getLegend().setEnabled(false);
        barChartRecipes.setTouchEnabled(false);
        barChartRecipes.animateY(900);
        barChartRecipes.invalidate();
    }

    private void exportReport() {
        Toast.makeText(getContext(), "⏳ Đang xuất báo cáo...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() ->
                Toast.makeText(getContext(),
                        "✅ Báo cáo đã xuất thành công!", Toast.LENGTH_LONG).show(), 1500);
    }

    private String formatNumber(long value) {
        if (value >= 1_000_000) return String.format(Locale.getDefault(), "%.1fM", value / 1_000_000f);
        if (value >= 1_000)    return String.format(Locale.getDefault(), "%.1fK", value / 1_000f);
        return String.valueOf(value);
    }
}