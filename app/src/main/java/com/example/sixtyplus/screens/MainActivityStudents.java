package com.example.sixtyplus.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.views.CircularProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivityStudents extends BaseActivity {

    private CircularProgressView circularProgress;
    private TextView tvGreeting, tvStudentName, tvHoursText, tvDaysLeft;
    private ProgressBar progressBarDays;
    private TableLayout tableUpcoming;
    private UserStudent currentStudent;

    private static final int TARGET_MONTH = Calendar.JUNE;
    private static final int TARGET_DAY = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_students);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        circularProgress = findViewById(R.id.circularProgress);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvHoursText = findViewById(R.id.tvHoursText);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
        progressBarDays = findViewById(R.id.progressBarDays);
        tableUpcoming = findViewById(R.id.tableUpcoming);

        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);
        if (currentStudent != null) {
            tvGreeting.setText(getGreeting() + "!");
            tvStudentName.setText(currentStudent.getFirstName() + " " + currentStudent.getLastName());
        }

        setupDaysProgress();
        loadVolunteeringData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDaysProgress();
        loadVolunteeringData();
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) return "בוקר טוב";
        else if (hour >= 12 && hour < 17) return "צהריים טובים";
        else if (hour >= 17 && hour < 21) return "ערב טוב";
        else return "לילה טוב";
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

    private void loadVolunteeringData() {
        if (currentStudent == null) return;

        databaseService.getVolunteeringList(
                new DatabaseService.DatabaseCallback<List<Volunteering>>() {
                    @Override
                    public void onCompleted(List<Volunteering> volunteeringList) {
                        float totalHours = 0f;

                        java.util.Map<String, List<Volunteering>> dayMap = new java.util.LinkedHashMap<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                        Calendar cal = Calendar.getInstance();
                        String[] next3Days = new String[3];

                        for (int i = 0; i < 3; i++) {
                            next3Days[i] = sdf.format(cal.getTime());
                            dayMap.put(next3Days[i], new ArrayList<>());
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        for (Volunteering v : volunteeringList) {
                            if (v.getStudentId() == null ||
                                    !v.getStudentId().equals(currentStudent.getId()))
                                continue;

                            String status = v.getStatus();

                            if ("approved".equals(status) || "completed".equals(status)) {
                                totalHours += v.getTotalHours();
                            }

                            if ("approved".equals(status)) {
                                String dateKey = sdf.format(new Date(v.getDateMillis()));
                                if (dayMap.containsKey(dateKey)) {
                                    dayMap.get(dateKey).add(v);
                                }
                            }
                        }

                        final float finalHours = totalHours;
                        final java.util.Map<String, List<Volunteering>> finalDayMap = dayMap;
                        final String[] finalDays = next3Days;

                        runOnUiThread(() -> {
                            circularProgress.setHours(finalHours);

                            int percent = (int) Math.min((finalHours / 60f) * 100, 100);
                            float remaining = Math.max(0, 60f - finalHours);
                            tvHoursText.setText("השלמת " + percent +
                                    "% משעות ההתנדבות שלך\nנשארו לך " +
                                    String.format("%.1f", remaining) + " שעות מתוך 60");

                            updateTable(finalDays, finalDayMap);
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {}
                });
    }

    private void updateTable(String[] days,
                             java.util.Map<String, List<Volunteering>> dayMap) {
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

                // צבע שורות סירוגי
                int bgColor = rowIndex % 2 == 0
                        ? Color.WHITE
                        : Color.parseColor("#FAFAFA");
                tv.setBackgroundColor(bgColor);

                if (list == null || list.isEmpty()) {
                    if (rowIndex == 0) {
                        tv.setText("אין התנדבות\nביום זה");
                        tv.setTextColor(Color.parseColor("#AAAAAA"));
                    } else {
                        tv.setText("");
                    }
                } else if (rowIndex < list.size()) {
                    Volunteering v = list.get(rowIndex);
                    tv.setText(v.getPlaceName() + "\n"
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