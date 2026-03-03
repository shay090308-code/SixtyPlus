package com.example.sixtyplus.screens;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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

public class ScheduleInCharge extends BaseActivity {

    private LinearLayout llDaysContainer;
    private TextView tvWeekRange;
    private UserInCharge currentUser;

    private static final String[] HEBREW_DAYS = {
            "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_in_charge);

        llDaysContainer = findViewById(R.id.llDaysContainer);
        tvWeekRange = findViewById(R.id.tvWeekRange);

        currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);

        setupWeekRange();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void setupWeekRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_YEAR, 7);
        tvWeekRange.setText(sdf.format(start.getTime()) + " - " + sdf.format(end.getTime()));
    }

    private void loadData() {
        if (currentUser == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Map<String, DayData> weekMap = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 8; i++) {
            String dateKey = sdf.format(cal.getTime());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            String hebrewDay = "יום " + HEBREW_DAYS[dayOfWeek];
            weekMap.put(dateKey, new DayData(hebrewDay, dateKey, new ArrayList<>()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        databaseService.getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> list) {
                for (Volunteering v : list) {
                    if (!"approved".equals(v.getStatus())) continue;
                    if (v.getPlaceId() == null || !v.getPlaceId().equals(currentUser.getId())) continue;

                    String dateKey = sdf.format(new Date(v.getDateMillis()));
                    if (weekMap.containsKey(dateKey)) {
                        weekMap.get(dateKey).volunteerings.add(v);
                    }
                }

                for (DayData day : weekMap.values()) {
                    day.volunteerings.sort((a, b) -> {
                        int aMin = a.getStartTime().getHour() * 60 + a.getStartTime().getMinute();
                        int bMin = b.getStartTime().getHour() * 60 + b.getStartTime().getMinute();
                        return Integer.compare(aMin, bMin);
                    });
                }

                runOnUiThread(() -> buildWeekUI(weekMap));
            }

            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void buildWeekUI(Map<String, DayData> weekMap) {
        llDaysContainer.removeAllViews();

        float dp = getResources().getDisplayMetrics().density;

        for (DayData day : weekMap.values()) {
            boolean hasVolunteering = !day.volunteerings.isEmpty();

            androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, (int)(20 * dp));
            card.setLayoutParams(cardParams);
            card.setRadius(20f * dp);
            card.setCardElevation(hasVolunteering ? 10f * dp : 4f * dp);
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            card.addView(cardContent);

            // כותרת יום
            LinearLayout dayHeader = new LinearLayout(this);
            dayHeader.setOrientation(LinearLayout.HORIZONTAL);
            dayHeader.setBackgroundColor(hasVolunteering
                    ? Color.parseColor("#F0D238")
                    : Color.parseColor("#F4F4F4"));
            dayHeader.setPadding((int)(20*dp), (int)(16*dp), (int)(20*dp), (int)(16*dp));
            dayHeader.setGravity(Gravity.CENTER_VERTICAL);
            cardContent.addView(dayHeader);

            if (hasVolunteering) {
                TextView tvDot = new TextView(this);
                tvDot.setText("●");
                tvDot.setTextColor(Color.parseColor("#1A1A2E"));
                tvDot.setTextSize(10f);
                LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                dotParams.setMargins(0, 0, (int)(8*dp), 0);
                tvDot.setLayoutParams(dotParams);
                dayHeader.addView(tvDot);
            }

            TextView tvDayName = new TextView(this);
            tvDayName.setText(day.hebrewDayName);
            tvDayName.setTextColor(hasVolunteering
                    ? Color.parseColor("#1A1A2E")
                    : Color.parseColor("#999999"));
            tvDayName.setTextSize(15f);
            tvDayName.setTypeface(null, hasVolunteering ? Typeface.BOLD : Typeface.NORMAL);
            tvDayName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            dayHeader.addView(tvDayName);

            TextView tvDate = new TextView(this);
            tvDate.setText(day.dateStr);
            tvDate.setTextColor(hasVolunteering
                    ? Color.parseColor("#555555")
                    : Color.parseColor("#BBBBBB"));
            tvDate.setTextSize(13f);
            dayHeader.addView(tvDate);

            // תוכן
            if (!hasVolunteering) {
                TextView tvEmpty = new TextView(this);
                tvEmpty.setText("אין התנדבויות");
                tvEmpty.setTextColor(Color.parseColor("#CCCCCC"));
                tvEmpty.setTextSize(13f);
                tvEmpty.setGravity(Gravity.CENTER);
                tvEmpty.setPadding((int)(32*dp), (int)(20*dp), (int)(32*dp), (int)(20*dp));
                cardContent.addView(tvEmpty);
            } else {
                for (int i = 0; i < day.volunteerings.size(); i++) {
                    Volunteering v = day.volunteerings.get(i);

                    if (i > 0) {
                        LinearLayout dividerLayout = new LinearLayout(this);
                        dividerLayout.setOrientation(LinearLayout.HORIZONTAL);
                        dividerLayout.setGravity(Gravity.CENTER_VERTICAL);
                        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        divParams.setMargins((int)(20*dp), 0, (int)(20*dp), 0);
                        dividerLayout.setLayoutParams(divParams);

                        View line1 = new View(this);
                        line1.setBackgroundColor(Color.parseColor("#F0D238"));
                        line1.setLayoutParams(new LinearLayout.LayoutParams((int)(24*dp), (int)(2*dp)));
                        dividerLayout.addView(line1);

                        View line2 = new View(this);
                        line2.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        LinearLayout.LayoutParams line2Params = new LinearLayout.LayoutParams(0, (int)(1*dp), 1f);
                        line2Params.setMargins((int)(8*dp), 0, 0, 0);
                        line2.setLayoutParams(line2Params);
                        dividerLayout.addView(line2);

                        cardContent.addView(dividerLayout);
                    }

                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding((int)(20*dp), (int)(18*dp), (int)(20*dp), (int)(18*dp));
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setBackgroundColor(Color.WHITE);
                    cardContent.addView(row);

                    // אייקון
                    TextView tvIcon = new TextView(this);
                    tvIcon.setText("👤");
                    tvIcon.setTextSize(15f);
                    tvIcon.setGravity(Gravity.CENTER);
                    tvIcon.setBackground(createCircle("#1A1A2E"));
                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                            (int)(44*dp), (int)(44*dp));
                    iconParams.setMargins(0, 0, (int)(16*dp), 0);
                    tvIcon.setLayoutParams(iconParams);
                    row.addView(tvIcon);

                    // פרטים — שם תלמיד + משך
                    LinearLayout details = new LinearLayout(this);
                    details.setOrientation(LinearLayout.VERTICAL);
                    details.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    row.addView(details);

                    TextView tvStudent = new TextView(this);
                    tvStudent.setText(v.getStudentName());
                    tvStudent.setTextColor(Color.parseColor("#222222"));
                    tvStudent.setTextSize(14f);
                    tvStudent.setTypeface(null, Typeface.BOLD);
                    details.addView(tvStudent);

                    TextView tvDuration = new TextView(this);
                    tvDuration.setText(v.getFormattedDuration());
                    tvDuration.setTextColor(Color.parseColor("#888888"));
                    tvDuration.setTextSize(12f);
                    details.addView(tvDuration);

                    // שעות
                    TextView tvTime = new TextView(this);
                    tvTime.setText(v.getStartTime().toString() + "\n" + v.getEndTime().toString());
                    tvTime.setTextColor(Color.parseColor("#666666"));
                    tvTime.setTextSize(12f);
                    tvTime.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                    row.addView(tvTime);
                }
            }

            llDaysContainer.addView(card);
        }
    }

    private android.graphics.drawable.GradientDrawable createCircle(String color) {
        android.graphics.drawable.GradientDrawable drawable =
                new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(color));
        return drawable;
    }

    private static class DayData {
        String hebrewDayName;
        String dateStr;
        List<Volunteering> volunteerings;

        DayData(String hebrewDayName, String dateStr, List<Volunteering> volunteerings) {
            this.hebrewDayName = hebrewDayName;
            this.dateStr = dateStr;
            this.volunteerings = volunteerings;
        }
    }
}