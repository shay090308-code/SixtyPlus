package com.example.sixtyplus.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivityInCharge extends BaseActivity {

    private TextView tvGreeting, tvInChargeName, tvPendingCount, tvPendingText, tvDaysLeft;
    private ProgressBar progressBarDays;
    private TableLayout tableUpcoming;
    private UserInCharge currentUser;

    private static final int TARGET_MONTH = Calendar.JUNE;
    private static final int TARGET_DAY = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_in_charge);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvInChargeName = findViewById(R.id.tvInChargeName);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvPendingText = findViewById(R.id.tvPendingText);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
        progressBarDays = findViewById(R.id.progressBarDays);
        tableUpcoming = findViewById(R.id.tableUpcoming);

        currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);

        setupGreeting();
        setupUserName();
        setupDaysProgress();
        loadData();

        findViewById(R.id.btnGoToRequests).setOnClickListener(v ->
                navigateTo(AcceptingVolunteers.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDaysProgress();
        loadData();
    }

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) greeting = "בוקר טוב!";
        else if (hour >= 12 && hour < 17) greeting = "צהריים טובים!";
        else if (hour >= 17 && hour < 21) greeting = "ערב טוב!";
        else greeting = "לילה טוב!";
        tvGreeting.setText(greeting);
    }

    private void setupUserName() {
        if (currentUser != null) {
            tvInChargeName.setText(currentUser.getPlaceName());
        }
    }

    private void setupDaysProgress() {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.MONTH, TARGET_MONTH);
        target.set(Calendar.DAY_OF_MONTH, TARGET_DAY);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);

        if (today.after(target)) {
            target.add(Calendar.YEAR, 1);
        }

        long diffMillis = target.getTimeInMillis() - today.getTimeInMillis();
        int daysLeft = (int) (diffMillis / (1000 * 60 * 60 * 24));

        int totalDays = 300;
        int progress = Math.max(0, 100 - (int) ((daysLeft / (float) totalDays) * 100));

        progressBarDays.setProgress(progress);
        tvDaysLeft.setText("נותרו " + daysLeft + " ימים לסיום שנת הלימודים");
    }

    private void loadData() {
        if (currentUser == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String[] next3Days = new String[3];
        Map<String, List<Volunteering>> dayMap = new LinkedHashMap<>();

        for (int i = 0; i < 3; i++) {
            next3Days[i] = sdf.format(cal.getTime());
            dayMap.put(next3Days[i], new ArrayList<>());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        databaseService.getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> allVolunteering) {
                int pendingCount = 0;

                for (Volunteering v : allVolunteering) {
                    if (v.getPlaceId() == null || !v.getPlaceId().equals(currentUser.getId()))
                        continue;

                    if ("pending".equals(v.getStatus())) {
                        pendingCount++;
                    }

                    if ("approved".equals(v.getStatus())) {
                        String dateKey = sdf.format(new Date(v.getDateMillis()));
                        if (dayMap.containsKey(dateKey)) {
                            dayMap.get(dateKey).add(v);
                        }
                    }
                }

                // מיון כל יום לפי שעת התחלה
                for (List<Volunteering> list : dayMap.values()) {
                    list.sort((a, b) -> {
                        int aMin = a.getStartTime().getHour() * 60 + a.getStartTime().getMinute();
                        int bMin = b.getStartTime().getHour() * 60 + b.getStartTime().getMinute();
                        return Integer.compare(aMin, bMin);
                    });
                }

                final int finalPending = pendingCount;
                runOnUiThread(() -> {
                    tvPendingCount.setText(String.valueOf(finalPending));
                    tvPendingText.setText(finalPending == 0
                            ? "אין בקשות ממתינות כרגע"
                            : "בקשות מחכות לאישורך");
                    updateTable(next3Days, dayMap);
                });
            }

            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void updateTable(String[] days, Map<String, List<Volunteering>> dayMap) {
        tableUpcoming.removeAllViews();

        // שורת כותרת
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#1A1A2E"));

        for (String day : days) {
            TextView tvHeader = new TextView(this);
            tvHeader.setText(day);
            tvHeader.setGravity(Gravity.CENTER);
            tvHeader.setPadding(16, 20, 16, 20);
            tvHeader.setBackgroundColor(Color.parseColor("#1A1A2E"));
            tvHeader.setTextColor(Color.parseColor("#F0D238"));
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            tvHeader.setTextSize(14f);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(1, 1, 1, 1);
            tvHeader.setLayoutParams(params);
            headerRow.addView(tvHeader);
        }
        tableUpcoming.addView(headerRow);

        // מספר שורות מקסימלי
        int maxRows = 1;
        for (List<Volunteering> list : dayMap.values()) {
            if (list.size() > maxRows) maxRows = list.size();
        }

        // שורות תוכן
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor("#E0E0E0"));

            for (String day : days) {
                List<Volunteering> list = dayMap.get(day);
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setPadding(16, 20, 16, 20);
                tv.setTextColor(Color.parseColor("#222222"));
                tv.setTextSize(13f);

                int bgColor = rowIndex % 2 == 0 ? Color.WHITE : Color.parseColor("#FAFAFA");
                tv.setBackgroundColor(bgColor);

                if (list == null || list.isEmpty()) {
                    if (rowIndex == 0) {
                        tv.setText("אין התנדבויות\nביום זה");
                        tv.setTextColor(Color.parseColor("#AAAAAA"));
                    } else {
                        tv.setText("");
                    }
                } else if (rowIndex < list.size()) {
                    Volunteering v = list.get(rowIndex);
                    tv.setText(v.getStudentName() + "\n"
                            + v.getStartTime().toString()
                            + "-" + v.getEndTime().toString());

                    if (rowIndex < list.size() - 1) {
                        tv.setBackground(createDividerBackground());
                    }
                } else {
                    tv.setText("");
                }

                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                params.setMargins(1, 1, 1, 1);
                tv.setLayoutParams(params);
                row.addView(tv);
            }

            tableUpcoming.addView(row);
        }
    }

    private android.graphics.drawable.GradientDrawable createDividerBackground() {
        android.graphics.drawable.GradientDrawable drawable =
                new android.graphics.drawable.GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setStroke(1, Color.parseColor("#E0E0E0"));
        return drawable;
    }
}