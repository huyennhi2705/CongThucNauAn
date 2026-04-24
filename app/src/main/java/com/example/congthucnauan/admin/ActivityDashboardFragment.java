package com.example.congthucnauan.admin;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.congthucnauan.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityDashboardFragment extends Fragment {

    // ── Header views ───────────────────────────────────────────────────────
    private TextView tvGreeting, tvAdminName, tvCurrentDate, tvNotificationBadge;

    // ── Stat cards ─────────────────────────────────────────────────────────
    private TextView tvUserCount, tvUserTrend;
    private TextView tvRecipeCount, tvRecipeTrend;
    private TextView tvFavoriteCount, tvFavoriteTrend;
    private TextView tvRatingAvg, tvRatingTrend;
    private ProgressBar progressUsers, progressRecipes, progressFavorites, progressRatings;

    // ── Distribution bars ──────────────────────────────────────────────────
    private View barMainDish, barDessert, barAppetizer, barDrink;
    private TextView tvMainDishPct, tvDessertPct, tvAppetizerPct, tvDrinkPct;

    // ── Chart ──────────────────────────────────────────────────────────────
    private BarChart barChartViews;
    private TextView tvTotalViews;

    // ── Period tabs ────────────────────────────────────────────────────────
    private TextView tabWeek, tabMonth, tabYear;
    private String selectedPeriod = "week";

    // ── Quick actions ──────────────────────────────────────────────────────
    private View btnAddRecipe, btnManageUsers, btnExportReport;

    // ── Category cards ─────────────────────────────────────────────────────
    private CardView cardCategoryMain, cardCategoryAppetizer,
            cardCategoryDessert, cardCategoryDrink, cardCategoryBreakfast;

    // ── Data sets per period ───────────────────────────────────────────────
    // Week data: T2→CN
    private static final float[] WEEK_DATA =
            {32500f, 41200f, 52100f, 38700f, 45300f, 29800f, 9000f};
    // Month data (4 tuần)
    private static final float[] MONTH_DATA =
            {210000f, 248590f, 195000f, 267000f};
    // Year data (4 quý)
    private static final float[] YEAR_DATA =
            {820000f, 950000f, 1100000f, 1320000f};

    // ──────────────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupHeader();
        setupStatCards();
        setupCategoryDistribution();
        setupBarChart(WEEK_DATA, new String[]{"T2","T3","T4","T5","T6","T7","CN"});
        setupPeriodTabs();
        setupCategoryCards();
        setupQuickActions();
    }

    // ── Bind all views ─────────────────────────────────────────────────────

    private void bindViews(View v) {
        // Header
        tvGreeting          = v.findViewById(R.id.tvGreeting);
        tvAdminName         = v.findViewById(R.id.tvAdminName);
        tvCurrentDate       = v.findViewById(R.id.tvCurrentDate);
        tvNotificationBadge = v.findViewById(R.id.tvNotificationBadge);

        // Stat cards
        tvUserCount     = v.findViewById(R.id.tvUserCount);
        tvUserTrend     = v.findViewById(R.id.tvUserTrend);
        tvRecipeCount   = v.findViewById(R.id.tvRecipeCount);
        tvRecipeTrend   = v.findViewById(R.id.tvRecipeTrend);
        tvFavoriteCount = v.findViewById(R.id.tvFavoriteCount);
        tvFavoriteTrend = v.findViewById(R.id.tvFavoriteTrend);
        tvRatingAvg     = v.findViewById(R.id.tvRatingAvg);
        tvRatingTrend   = v.findViewById(R.id.tvRatingTrend);
        progressUsers     = v.findViewById(R.id.progressUsers);
        progressRecipes   = v.findViewById(R.id.progressRecipes);
        progressFavorites = v.findViewById(R.id.progressFavorites);
        progressRatings   = v.findViewById(R.id.progressRatings);

        // Distribution bars
        barMainDish  = v.findViewById(R.id.barMainDish);
        barDessert   = v.findViewById(R.id.barDessert);
        barAppetizer = v.findViewById(R.id.barAppetizer);
        barDrink     = v.findViewById(R.id.barDrink);
        tvMainDishPct  = v.findViewById(R.id.tvMainDishPct);
        tvDessertPct   = v.findViewById(R.id.tvDessertPct);
        tvAppetizerPct = v.findViewById(R.id.tvAppetizerPct);
        tvDrinkPct     = v.findViewById(R.id.tvDrinkPct);

        // Chart
        barChartViews = v.findViewById(R.id.barChartViews);
        tvTotalViews  = v.findViewById(R.id.tvTotalViews);

        // Tabs
        tabWeek  = v.findViewById(R.id.tabWeek);
        tabMonth = v.findViewById(R.id.tabMonth);
        tabYear  = v.findViewById(R.id.tabYear);

        // Category cards
        cardCategoryMain      = v.findViewById(R.id.cardCategoryMain);
        cardCategoryAppetizer = v.findViewById(R.id.cardCategoryAppetizer);
        cardCategoryDessert   = v.findViewById(R.id.cardCategoryDessert);
        cardCategoryDrink     = v.findViewById(R.id.cardCategoryDrink);
        cardCategoryBreakfast = v.findViewById(R.id.cardCategoryBreakfast);

        // Quick actions
        btnAddRecipe    = v.findViewById(R.id.btnAddRecipe);
        btnManageUsers  = v.findViewById(R.id.btnManageUsers);
        btnExportReport = v.findViewById(R.id.btnExportReport);

        // Notification + menu buttons
        v.findViewById(R.id.btnNotification).setOnClickListener(vv ->
                Toast.makeText(requireContext(), "Thông báo", Toast.LENGTH_SHORT).show());
        v.findViewById(R.id.btnMenu).setOnClickListener(vv ->
                Toast.makeText(requireContext(), "Menu", Toast.LENGTH_SHORT).show());
    }

    // ── Header ─────────────────────────────────────────────────────────────

    private void setupHeader() {
        // Dynamic greeting by hour
        int hour = new java.util.Calendar.Builder().build().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            tvGreeting.setText("Chào buổi sáng, 👋");
        } else if (hour < 18) {
            tvGreeting.setText("Chào buổi chiều, 👋");
        } else {
            tvGreeting.setText("Chào buổi tối, 👋");
        }

        tvAdminName.setText("Quản trị viên");

        // Dynamic date in Vietnamese
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM, yyyy",
                new Locale("vi", "VN"));
        tvCurrentDate.setText(sdf.format(new Date()));

        tvNotificationBadge.setText("3");
    }

    // ── Stat cards with count-up animation ────────────────────────────────

    private void setupStatCards() {
        animateCount(tvUserCount, 0, 12847, "%,d");
        animateCount(tvRecipeCount, 0, 3421, "%,d");
        animateCount(tvFavoriteCount, 0, 89310, "%,d");
        animateFloat(tvRatingAvg, 0f, 4.8f);

        animateProgress(progressUsers, 72);
        animateProgress(progressRecipes, 58);
        animateProgress(progressFavorites, 89);
        animateProgress(progressRatings, 96);

        // Trend labels already set in XML; can update from server here
        tvUserTrend.setText("▲ 12%");
        tvRecipeTrend.setText("▲ 8%");
        tvFavoriteTrend.setText("▼ 3%");
        tvRatingTrend.setText("▲ 5%");
    }

    /** Animate an integer count-up in a TextView */
    private void animateCount(TextView tv, int from, int to, String fmt) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.setDuration(1200);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> {
            int val = (int) a.getAnimatedValue();
            tv.setText(String.format(Locale.getDefault(), "%,d", val));
        });
        anim.start();
    }

    /** Animate a float value (for rating) */
    private void animateFloat(TextView tv, float from, float to) {
        ValueAnimator anim = ValueAnimator.ofFloat(from, to);
        anim.setDuration(1200);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a ->
                tv.setText(String.format(Locale.getDefault(), "%.1f",
                        (float) a.getAnimatedValue())));
        anim.start();
    }

    /** Animate ProgressBar from 0 to target */
    private void animateProgress(ProgressBar pb, int target) {
        ValueAnimator anim = ValueAnimator.ofInt(0, target);
        anim.setDuration(1000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> pb.setProgress((int) a.getAnimatedValue()));
        anim.start();
    }

    // ── Category distribution bars ─────────────────────────────────────────

    private void setupCategoryDistribution() {
        // Percentages: Món chính 39%, Tráng miệng 24%, Khai vị 15%, Đồ uống 9%
        // Bars animate after layout is complete
        barMainDish.post(() -> {
            animateBarWidth(barMainDish, 0.39f);
            animateBarWidth(barDessert, 0.24f);
            animateBarWidth(barAppetizer, 0.15f);
            animateBarWidth(barDrink, 0.09f);
        });
        tvMainDishPct.setText("39%");
        tvDessertPct.setText("24%");
        tvAppetizerPct.setText("15%");
        tvDrinkPct.setText("9%");
    }

    /**
     * Animates a bar View's width as a fraction of its parent's width.
     * The bar's parent is a FrameLayout that fills remaining space.
     */
    private void animateBarWidth(View bar, float fraction) {
        View parent = (View) bar.getParent();
        int parentWidth = parent.getWidth();
        int targetPx = (int) (parentWidth * fraction);

        ValueAnimator anim = ValueAnimator.ofInt(0, targetPx);
        anim.setDuration(900);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> {
            ViewGroup.LayoutParams lp = bar.getLayoutParams();
            lp.width = (int) a.getAnimatedValue();
            bar.setLayoutParams(lp);
        });
        anim.start();
    }

    // ── MPAndroidChart BarChart ────────────────────────────────────────────

    private void setupBarChart(float[] values, String[] labels) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Lượt xem");
        dataSet.setColors(buildBarColors(values, Color.parseColor("#E8631A"), Color.parseColor("#FFD5B8")));
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.55f);
        barChartViews.setData(data);

        // X Axis
        XAxis xAxis = barChartViews.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(labels.length);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.length) ? labels[idx] : "";
            }
        });
        xAxis.setTextColor(Color.parseColor("#BBBBBB"));
        xAxis.setTextSize(10f);

        // Y Axis
        YAxis leftAxis = barChartViews.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.parseColor("#BBBBBB"));
        leftAxis.setTextSize(9f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 1000) return (int)(value/1000) + "K";
                return String.valueOf((int)value);
            }
        });
        barChartViews.getAxisRight().setEnabled(false);

        // General chart config
        barChartViews.getDescription().setEnabled(false);
        barChartViews.getLegend().setEnabled(false);
        barChartViews.setDrawGridBackground(false);
        barChartViews.setDrawBorders(false);
        barChartViews.setTouchEnabled(false);
        barChartViews.setExtraBottomOffset(4f);
        barChartViews.animateY(900);
        barChartViews.invalidate();

        // Update total label
        float total = 0;
        for (float v : values) total += v;
        tvTotalViews.setText(formatViewCount(total) + " lượt");
    }

    /** Highlight the max bar with primary color, others lighter */
    private int[] buildBarColors(float[] values, int highlight, int normal) {
        float max = 0;
        for (float v : values) if (v > max) max = v;
        int[] colors = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            colors[i] = (values[i] == max) ? highlight : normal;
        }
        return colors;
    }

    private String formatViewCount(float value) {
        if (value >= 1_000_000) return String.format(Locale.getDefault(), "%.1fM", value/1_000_000);
        if (value >= 1_000)    return String.format(Locale.getDefault(), "%.1fK", value/1_000);
        return String.valueOf((int)value);
    }

    // ── Period tabs ────────────────────────────────────────────────────────

    private void setupPeriodTabs() {
        tabWeek.setOnClickListener(v -> switchTab("week"));
        tabMonth.setOnClickListener(v -> switchTab("month"));
        tabYear.setOnClickListener(v -> switchTab("year"));
        updateTabUI("week");
    }

    private void switchTab(String period) {
        if (period.equals(selectedPeriod)) return;
        selectedPeriod = period;
        updateTabUI(period);

        switch (period) {
            case "week":
                setupBarChart(WEEK_DATA,
                        new String[]{"T2","T3","T4","T5","T6","T7","CN"});
                break;
            case "month":
                setupBarChart(MONTH_DATA,
                        new String[]{"Tuần 1","Tuần 2","Tuần 3","Tuần 4"});
                break;
            case "year":
                setupBarChart(YEAR_DATA,
                        new String[]{"Quý 1","Quý 2","Quý 3","Quý 4"});
                break;
        }
    }

    private void updateTabUI(String selected) {
        int activeColor   = Color.WHITE;
        int inactiveColor = Color.parseColor("#888888");

        // Reset all tabs
        resetTab(tabWeek,  inactiveColor);
        resetTab(tabMonth, inactiveColor);
        resetTab(tabYear,  inactiveColor);

        // Activate selected
        switch (selected) {
            case "week":
                activateTab(tabWeek,  activeColor); break;
            case "month":
                activateTab(tabMonth, activeColor); break;
            case "year":
                activateTab(tabYear,  activeColor); break;
        }
    }

    private void activateTab(TextView tab, int textColor) {
        tab.setTextColor(textColor);
        tab.setBackgroundResource(R.drawable.bg_tab_selected);
    }

    private void resetTab(TextView tab, int textColor) {
        tab.setTextColor(textColor);
        tab.setBackground(null);
    }

    // ── Category card clicks ───────────────────────────────────────────────

    private void setupCategoryCards() {
        cardCategoryMain.setOnClickListener(v ->
                navigateToCategory("Món chính"));
        cardCategoryAppetizer.setOnClickListener(v ->
                navigateToCategory("Khai vị"));
        cardCategoryDessert.setOnClickListener(v ->
                navigateToCategory("Tráng miệng"));
        cardCategoryDrink.setOnClickListener(v ->
                navigateToCategory("Đồ uống"));
        cardCategoryBreakfast.setOnClickListener(v ->
                navigateToCategory("Ăn sáng"));

        // "Xem tất cả" category link
        View root = requireView();
        root.findViewById(R.id.tvSeeAllCategories).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Tất cả danh mục", Toast.LENGTH_SHORT).show());
        root.findViewById(R.id.tvSeeAllRecipes).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Tất cả công thức nổi bật", Toast.LENGTH_SHORT).show());
    }

    private void navigateToCategory(String name) {
        Toast.makeText(requireContext(), "Danh mục: " + name, Toast.LENGTH_SHORT).show();
        // Replace with Navigation.findNavController(requireView()).navigate(...)
    }

    // ── Quick action buttons ───────────────────────────────────────────────

    private void setupQuickActions() {
        btnAddRecipe.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Thêm công thức mới", Toast.LENGTH_SHORT).show();
            // Navigate to AddRecipeFragment
        });

        btnManageUsers.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Quản lý người dùng", Toast.LENGTH_SHORT).show();
            // Navigate to UserManagementFragment
        });

        btnExportReport.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đang xuất báo cáo...", Toast.LENGTH_SHORT).show();
            exportReport();
        });
    }

    private void exportReport() {
        // TODO: Generate PDF/Excel report
        // Example: use Apache POI or PdfDocument API
        new android.os.Handler().postDelayed(() ->
                        Toast.makeText(requireContext(),
                                "✅ Báo cáo đã xuất thành công!", Toast.LENGTH_LONG).show(),
                1500);
    }
}