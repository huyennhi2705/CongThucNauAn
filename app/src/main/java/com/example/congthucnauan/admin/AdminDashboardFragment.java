package com.example.congthucnauan.admin;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.congthucnauan.R;
import com.example.congthucnauan.models.Category;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private static final String DB_URL = "https://ctna-996dc-default-rtdb.firebaseio.com/";

    // ── Header ──
    private TextView tvAdminName, tvCurrentDate, tvNotificationBadge;

    // ── Stat cards ──
    private TextView tvUserCount, tvUserTrend;
    private TextView tvRecipeCount, tvRecipeTrend;
    private TextView tvFavoriteCount, tvFavoriteTrend;
    private TextView tvRatingAvg, tvRatingTrend;
    private ProgressBar progressUsers, progressRecipes, progressFavorites, progressRatings;

    // ── Chart ──
    private BarChart barChartViews;
    private TextView tvTotalViews;

    // ── Period tabs ──
    private TextView tabWeek, tabMonth, tabYear;
    private String selectedPeriod = "week";

    // ── Quick actions ──
    private View btnQLDM, btnManageUsers, btnExportReport;

    // ── Category dynamic container ──
    private LinearLayout llCategoryContainer;

    // ── Firebase ──
    private DatabaseReference categoryRef;
    private ValueEventListener categoryListener;

    // ── Chart data ──
    private static final float[] WEEK_DATA  = {32500f, 41200f, 52100f, 38700f, 45300f, 29800f, 9000f};
    private static final float[] MONTH_DATA = {210000f, 248590f, 195000f, 267000f};
    private static final float[] YEAR_DATA  = {820000f, 950000f, 1100000f, 1320000f};

    // ══════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupHeader();
        setupStatCards();
        setupBarChart(WEEK_DATA, new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"});
        setupPeriodTabs();
        loadCategoriesFromFirebase();   // ← Thay thế setupCategoryCards() cũ
        setupQuickActions();
        setupSeeAllButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Gỡ listener tránh memory leak
        if (categoryRef != null && categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Bind views
    // ══════════════════════════════════════════════════════════

    private void bindViews(View v) {
        tvAdminName          = v.findViewById(R.id.tvAdminName);
        tvCurrentDate        = v.findViewById(R.id.tvCurrentDate);
        tvNotificationBadge  = v.findViewById(R.id.tvNotificationBadge);

        tvUserCount          = v.findViewById(R.id.tvUserCount);
        tvUserTrend          = v.findViewById(R.id.tvUserTrend);
        tvRecipeCount        = v.findViewById(R.id.tvRecipeCount);
        tvRecipeTrend        = v.findViewById(R.id.tvRecipeTrend);
        tvFavoriteCount      = v.findViewById(R.id.tvFavoriteCount);
        tvFavoriteTrend      = v.findViewById(R.id.tvFavoriteTrend);
        tvRatingAvg          = v.findViewById(R.id.tvRatingAvg);
        tvRatingTrend        = v.findViewById(R.id.tvRatingTrend);

        progressUsers        = v.findViewById(R.id.progressUsers);
        progressRecipes      = v.findViewById(R.id.progressRecipes);
        progressFavorites    = v.findViewById(R.id.progressFavorites);
        progressRatings      = v.findViewById(R.id.progressRatings);

        barChartViews        = v.findViewById(R.id.barChartViews);
        tvTotalViews         = v.findViewById(R.id.tvTotalViews);

        tabWeek              = v.findViewById(R.id.tabWeek);
        tabMonth             = v.findViewById(R.id.tabMonth);
        tabYear              = v.findViewById(R.id.tabYear);

        llCategoryContainer  = v.findViewById(R.id.llCategoryContainer);

        btnQLDM              = v.findViewById(R.id.btnQLDM);
        btnManageUsers       = v.findViewById(R.id.btnManageUsers);
        btnExportReport      = v.findViewById(R.id.btnExportReport);

        v.findViewById(R.id.btnNotification).setOnClickListener(vv ->
                Toast.makeText(requireContext(), "Thông báo", Toast.LENGTH_SHORT).show());
    }

    // ══════════════════════════════════════════════════════════
    //  Header
    // ══════════════════════════════════════════════════════════

    private void setupHeader() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting = (hour < 12) ? "Chào buổi sáng 👋"
                : (hour < 18) ? "Chào buổi chiều 👋"
                : "Chào buổi tối 👋";
        if (tvAdminName != null) tvAdminName.setText(greeting);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM, yyyy", new Locale("vi", "VN"));
        if (tvCurrentDate != null) tvCurrentDate.setText(sdf.format(new Date()));
        if (tvNotificationBadge != null) tvNotificationBadge.setText("3");
    }

    // ══════════════════════════════════════════════════════════
    //  Stat cards
    // ══════════════════════════════════════════════════════════

    private void setupStatCards() {
        animateCount(tvUserCount, 0, 45820);
        animateCount(tvRecipeCount, 0, 12450);
        animateCount(tvFavoriteCount, 0, 289450);
        animateFloat(tvRatingAvg, 0f, 4.7f);

        animateProgress(progressUsers,     85);
        animateProgress(progressRecipes,   76);
        animateProgress(progressFavorites, 92);
        animateProgress(progressRatings,   94);

        if (tvUserTrend     != null) tvUserTrend.setText("▲ 18%");
        if (tvRecipeTrend   != null) tvRecipeTrend.setText("▲ 12%");
        if (tvFavoriteTrend != null) tvFavoriteTrend.setText("▲ 7%");
        if (tvRatingTrend   != null) tvRatingTrend.setText("▲ 4%");
    }

    private void animateCount(TextView tv, int from, int to) {
        if (tv == null) return;
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.setDuration(1400);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a ->
                tv.setText(String.format(Locale.getDefault(), "%,d", (int) a.getAnimatedValue())));
        anim.start();
    }

    private void animateFloat(TextView tv, float from, float to) {
        if (tv == null) return;
        ValueAnimator anim = ValueAnimator.ofFloat(from, to);
        anim.setDuration(1400);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a ->
                tv.setText(String.format(Locale.getDefault(), "%.1f", (float) a.getAnimatedValue())));
        anim.start();
    }

    private void animateProgress(ProgressBar pb, int target) {
        if (pb == null) return;
        ValueAnimator anim = ValueAnimator.ofInt(0, target);
        anim.setDuration(1200);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> pb.setProgress((int) a.getAnimatedValue()));
        anim.start();
    }

    // ══════════════════════════════════════════════════════════
    //  Bar chart
    // ══════════════════════════════════════════════════════════

    private void setupBarChart(float[] values, String[] labels) {
        if (barChartViews == null) return;

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) entries.add(new BarEntry(i, values[i]));

        BarDataSet dataSet = new BarDataSet(entries, "Lượt xem");
        dataSet.setColors(buildBarColors(values,
                Color.parseColor("#E8631A"), Color.parseColor("#FFD5B8")));
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.55f);
        barChartViews.setData(data);

        XAxis xAxis = barChartViews.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(labels.length);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.length) ? labels[idx] : "";
            }
        });
        xAxis.setTextColor(Color.parseColor("#BBBBBB"));
        xAxis.setTextSize(10f);

        YAxis leftAxis = barChartViews.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.parseColor("#BBBBBB"));
        leftAxis.setTextSize(9f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                return value >= 1000 ? (int)(value / 1000) + "K" : String.valueOf((int) value);
            }
        });

        barChartViews.getAxisRight().setEnabled(false);
        barChartViews.getDescription().setEnabled(false);
        barChartViews.getLegend().setEnabled(false);
        barChartViews.setDrawGridBackground(false);
        barChartViews.setDrawBorders(false);
        barChartViews.setTouchEnabled(false);
        barChartViews.animateY(900);
        barChartViews.invalidate();

        float total = 0;
        for (float val : values) total += val;
        if (tvTotalViews != null) tvTotalViews.setText(formatViewCount(total) + " lượt");
    }

    private int[] buildBarColors(float[] values, int highlight, int normal) {
        float max = 0;
        for (float v : values) if (v > max) max = v;
        int[] colors = new int[values.length];
        for (int i = 0; i < values.length; i++)
            colors[i] = (values[i] == max) ? highlight : normal;
        return colors;
    }

    private String formatViewCount(float value) {
        if (value >= 1_000_000) return String.format(Locale.getDefault(), "%.1fM", value / 1_000_000);
        if (value >= 1_000)     return String.format(Locale.getDefault(), "%.1fK", value / 1_000);
        return String.valueOf((int) value);
    }

    // ══════════════════════════════════════════════════════════
    //  Period tabs
    // ══════════════════════════════════════════════════════════

    private void setupPeriodTabs() {
        if (tabWeek  != null) tabWeek.setOnClickListener(v  -> switchTab("week"));
        if (tabMonth != null) tabMonth.setOnClickListener(v -> switchTab("month"));
        if (tabYear  != null) tabYear.setOnClickListener(v  -> switchTab("year"));
        updateTabUI("week");
    }

    private void switchTab(String period) {
        if (period.equals(selectedPeriod)) return;
        selectedPeriod = period;
        updateTabUI(period);
        switch (period) {
            case "week":
                setupBarChart(WEEK_DATA, new String[]{"T2","T3","T4","T5","T6","T7","CN"});
                break;
            case "month":
                setupBarChart(MONTH_DATA, new String[]{"Tuần 1","Tuần 2","Tuần 3","Tuần 4"});
                break;
            case "year":
                setupBarChart(YEAR_DATA, new String[]{"Quý 1","Quý 2","Quý 3","Quý 4"});
                break;
        }
    }

    private void updateTabUI(String selected) {
        resetTab(tabWeek,  Color.parseColor("#888888"));
        resetTab(tabMonth, Color.parseColor("#888888"));
        resetTab(tabYear,  Color.parseColor("#888888"));
        switch (selected) {
            case "week":  activateTab(tabWeek,  Color.WHITE); break;
            case "month": activateTab(tabMonth, Color.WHITE); break;
            case "year":  activateTab(tabYear,  Color.WHITE); break;
        }
    }

    private void activateTab(TextView tab, int color) {
        if (tab == null) return;
        tab.setTextColor(color);
        tab.setBackgroundResource(R.drawable.bg_tab_selected);
    }

    private void resetTab(TextView tab, int color) {
        if (tab == null) return;
        tab.setTextColor(color);
        tab.setBackground(null);
    }

    // ══════════════════════════════════════════════════════════
    //  Categories – Load động từ Firebase
    // ══════════════════════════════════════════════════════════

    private void loadCategoriesFromFirebase() {
        if (llCategoryContainer == null) return;

        categoryRef = FirebaseDatabase.getInstance(DB_URL).getReference("categories");

        categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || llCategoryContainer == null) return;

                llCategoryContainer.removeAllViews();

                boolean isFirst = true;
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Category cat = child.getValue(Category.class);
                        if (cat == null) continue;
                        if (cat.getId() == null) cat.setId(child.getKey());
                        if (cat.getName() == null) continue;

                        View card = buildCategoryCard(
                                cat.getName(),
                                cat.getIcon(),
                                isFirst
                        );
                        llCategoryContainer.addView(card);
                        isFirst = false;

                    } catch (Exception e) {
                        // bỏ qua item lỗi, tiếp tục
                    }
                }

                // Nếu không có danh mục nào → hiện placeholder
                if (llCategoryContainer.getChildCount() == 0) {
                    llCategoryContainer.addView(buildEmptyCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Lỗi tải danh mục: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        categoryRef.addValueEventListener(categoryListener);
    }

    /**
     * Tạo một CardView danh mục theo style giống XML cũ.
     * isSelected = true → nền cam (#E8631A), text trắng (card đầu tiên)
     */
    private View buildCategoryCard(String name, String iconName, boolean isSelected) {
        Context ctx = requireContext();
        float dp = ctx.getResources().getDisplayMetrics().density;

        // ── CardView ──
        CardView card = new CardView(ctx);
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams((int)(110 * dp), (int)(130 * dp));
        cardParams.setMarginEnd((int)(12 * dp));
        card.setLayoutParams(cardParams);
        card.setRadius(20 * dp);
        card.setCardElevation(5 * dp);
        card.setCardBackgroundColor(
                isSelected ? Color.parseColor("#E8631A") : Color.WHITE);
        card.setUseCompatPadding(true);

        // ── Inner LinearLayout ──
        LinearLayout inner = new LinearLayout(ctx);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setGravity(Gravity.CENTER);
        int pad = (int)(12 * dp);
        inner.setPadding(pad, pad, pad, pad);
        inner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // ── Emoji icon ──
        TextView tvEmoji = new TextView(ctx);
        LinearLayout.LayoutParams emojiParams =
                new LinearLayout.LayoutParams((int)(50 * dp), (int)(50 * dp));
        tvEmoji.setLayoutParams(emojiParams);
        tvEmoji.setGravity(Gravity.CENTER);
        tvEmoji.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        tvEmoji.setText(getEmojiForIcon(iconName));

        // ── Name ──
        TextView tvName = new TextView(ctx);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = (int)(8 * dp);
        tvName.setLayoutParams(nameParams);
        tvName.setGravity(Gravity.CENTER);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvName.setText(name);
        tvName.setTextColor(isSelected
                ? Color.WHITE
                : Color.parseColor("#1A1A2E"));
        tvName.setMaxLines(2);

        inner.addView(tvEmoji);
        inner.addView(tvName);
        card.addView(inner);

        // ── Click listener → navigate to category management ──
        card.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v)
                        .navigate(R.id.action_dashboard_to_category);
            } catch (Exception e) {
                Toast.makeText(ctx, "Danh mục: " + name, Toast.LENGTH_SHORT).show();
            }
        });

        return card;
    }

    /** Hiển thị khi Firebase chưa có dữ liệu danh mục */
    private View buildEmptyCategory() {
        Context ctx = requireContext();
        float dp = ctx.getResources().getDisplayMetrics().density;

        CardView card = new CardView(ctx);
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        (int)(130 * dp));
        p.setMarginEnd((int)(12 * dp));
        card.setLayoutParams(p);
        card.setRadius(20 * dp);
        card.setCardElevation(5 * dp);
        card.setCardBackgroundColor(Color.parseColor("#FFF0E8"));

        TextView tv = new TextView(ctx);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                (int)(160 * dp), LinearLayout.LayoutParams.MATCH_PARENT));
        tv.setGravity(Gravity.CENTER);
        tv.setText("Chưa có danh mục\nThêm tại Quản lý →");
        tv.setTextColor(Color.parseColor("#E8631A"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setPadding((int)(12*dp), 0, (int)(12*dp), 0);

        card.addView(tv);
        return card;
    }

    /**
     * Map iconName trong Firebase → emoji hiển thị.
     * Thêm case mới khi có icon mới trong CSDL.
     */
    private String getEmojiForIcon(String iconName) {
        if (iconName == null) return "🍽️";
        switch (iconName) {
            case "ic_food_main":    return "🍲";
            case "ic_food_dessert": return "🍰";
            case "ic_food_drink":   return "🥤";
            case "ic_food_snack":   return "🥗";
            case "ic_food_breakfast": return "🥐";
            case "ic_food_appetizer": return "🥗";
            case "ic_food_soup":    return "🍜";
            case "ic_food_grill":   return "🥩";
            case "ic_food_rice":    return "🍚";
            default:                return "🍽️";
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Quick actions
    // ══════════════════════════════════════════════════════════

    private void setupQuickActions() {
        if (btnQLDM != null) {
            btnQLDM.setOnClickListener(v ->
                    Navigation.findNavController(v)
                            .navigate(R.id.action_dashboard_to_category));
        }
        if (btnManageUsers != null) {
            btnManageUsers.setOnClickListener(v ->
                    Navigation.findNavController(v)
                            .navigate(R.id.action_dashboard_to_user));
        }
        if (btnExportReport != null) {
            btnExportReport.setOnClickListener(v -> exportReport());
        }
    }

    private void exportReport() {
        Toast.makeText(requireContext(), "Đang xuất báo cáo...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> {
            if (!isAdded()) return;
            Toast.makeText(requireContext(),
                    "✅ Xuất báo cáo thành công!", Toast.LENGTH_LONG).show();
        }, 1500);
    }

    // ══════════════════════════════════════════════════════════
    //  See all buttons
    // ══════════════════════════════════════════════════════════

    private void setupSeeAllButtons() {
        if (!isAdded()) return;
        View seeAllCat     = requireView().findViewById(R.id.tvSeeAllCategories);
        View seeAllRecipes = requireView().findViewById(R.id.tvSeeAllRecipes);

        if (seeAllCat != null) {
            seeAllCat.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_dashboard_to_category);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (seeAllRecipes != null) {
            seeAllRecipes.setOnClickListener(v ->
                    Toast.makeText(requireContext(),
                            "Tất cả công thức", Toast.LENGTH_SHORT).show());
        }
    }
}