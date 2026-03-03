package com.example.sixtyplus.screens;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CancelingVolunteers extends BaseActivity {

    private LinearLayout llVolunteeringContainer;
    private TextView tvNoVolunteering, tvDateRange;
    private UserInCharge currentUser;
    private List<Volunteering> volunteeringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canceling_volunteers);

        llVolunteeringContainer = findViewById(R.id.llVolunteeringContainer);
        tvNoVolunteering = findViewById(R.id.tvNoVolunteering);
        tvDateRange = findViewById(R.id.tvDateRange);

        currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);

        setupDateRange();
        loadVolunteering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVolunteering();
    }

    private void setupDateRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar[] range = getDateRange();
        tvDateRange.setText(sdf.format(range[0].getTime()) + " - " + sdf.format(range[1].getTime()));
    }

    // מחזיר [תחילת טווח, סוף טווח]
    // מיום שלישי הקודם (כולל) עד יום שלישי הבא (כולל)
    private Calendar[] getDateRange() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // מצא את יום שלישי הקודם (כולל היום אם היום שלישי)
        Calendar start = (Calendar) today.clone();
        while (start.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
            start.add(Calendar.DAY_OF_MONTH, -1);
        }

        // מצא את יום שלישי הבא (כולל היום אם היום שלישי)
        Calendar end = (Calendar) today.clone();
        if (end.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            end.add(Calendar.DAY_OF_MONTH, 7);
        } else {
            while (end.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
                end.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        return new Calendar[]{start, end};
    }

    private void loadVolunteering() {
        if (currentUser == null) return;

        Calendar[] range = getDateRange();
        long rangeStart = range[0].getTimeInMillis();
        long rangeEnd = range[1].getTimeInMillis();

        databaseService.getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> allList) {
                volunteeringList.clear();

                for (Volunteering v : allList) {
                    if (v.getPlaceId() == null || !v.getPlaceId().equals(currentUser.getId()))
                        continue;
                    if ("cancelled".equals(v.getStatus()))
                        continue;
                    if (v.getDateMillis() < rangeStart || v.getDateMillis() > rangeEnd)
                        continue;
                    volunteeringList.add(v);
                }

                // מיון לפי תאריך ואחר כך שעה
                Collections.sort(volunteeringList, (a, b) -> {
                    if (a.getDateMillis() != b.getDateMillis())
                        return Long.compare(a.getDateMillis(), b.getDateMillis());
                    int aMin = a.getStartTime().getHour() * 60 + a.getStartTime().getMinute();
                    int bMin = b.getStartTime().getHour() * 60 + b.getStartTime().getMinute();
                    return Integer.compare(aMin, bMin);
                });

                runOnUiThread(() -> buildUI());
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CancelingVolunteers.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildUI() {
        llVolunteeringContainer.removeAllViews();
        float dp = getResources().getDisplayMetrics().density;

        if (volunteeringList.isEmpty()) {
            tvNoVolunteering.setVisibility(View.VISIBLE);
            return;
        }

        tvNoVolunteering.setVisibility(View.GONE);

        SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String[] hebrewDays = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};

        for (int i = 0; i < volunteeringList.size(); i++) {
            Volunteering v = volunteeringList.get(i);

            // קו הפרדה בין פריטים
            if (i > 0) {
                LinearLayout dividerLayout = new LinearLayout(this);
                dividerLayout.setOrientation(LinearLayout.HORIZONTAL);
                dividerLayout.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                divParams.setMargins((int)(16*dp), 0, (int)(16*dp), 0);
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

                llVolunteeringContainer.addView(dividerLayout);
            }

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding((int)(4*dp), (int)(16*dp), (int)(4*dp), (int)(16*dp));
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundColor(Color.WHITE);
            llVolunteeringContainer.addView(row);

            // אייקון עיגול
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

            // פרטים
            LinearLayout details = new LinearLayout(this);
            details.setOrientation(LinearLayout.VERTICAL);
            details.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(details);

            // שם תלמיד
            TextView tvName = new TextView(this);
            tvName.setText(v.getStudentName());
            tvName.setTextColor(Color.parseColor("#222222"));
            tvName.setTextSize(14f);
            tvName.setTypeface(null, Typeface.BOLD);
            details.addView(tvName);

            // תאריך + יום בשבוע
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(v.getDateMillis());
            int dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
            TextView tvDate = new TextView(this);
            tvDate.setText("יום " + hebrewDays[dayIndex] + " | " + dateSdf.format(cal.getTime()));
            tvDate.setTextColor(Color.parseColor("#666666"));
            tvDate.setTextSize(12f);
            details.addView(tvDate);

            // שעות
            TextView tvTime = new TextView(this);
            tvTime.setText(v.getStartTime().toString() + " - " + v.getEndTime().toString());
            tvTime.setTextColor(Color.parseColor("#888888"));
            tvTime.setTextSize(12f);
            details.addView(tvTime);

            // כפתור ביטול
            TextView btnCancel = new TextView(this);
            btnCancel.setText("ביטול");
            btnCancel.setTextColor(Color.WHITE);
            btnCancel.setTextSize(13f);
            btnCancel.setTypeface(null, Typeface.BOLD);
            btnCancel.setPadding((int)(16*dp), (int)(10*dp), (int)(16*dp), (int)(10*dp));
            btnCancel.setBackground(createRoundedBackground("#F44336", (int)(12*dp)));
            btnCancel.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            btnParams.setMargins((int)(12*dp), 0, 0, 0);
            btnCancel.setLayoutParams(btnParams);
            btnCancel.setOnClickListener(view -> showCancelDialog(v));
            row.addView(btnCancel);
        }
    }

    private void showCancelDialog(Volunteering v) {
        new AlertDialog.Builder(this)
                .setTitle("ביטול התנדבות")
                .setMessage("האם אתה בטוח שברצונך לבטל את ההתנדבות של " +
                        v.getStudentName() + "?")
                .setPositiveButton("כן, בטל", (dialog, which) -> cancelVolunteering(v))
                .setNegativeButton("לא", null)
                .show();
    }

    private void cancelVolunteering(Volunteering v) {
        v.setStatus("cancelled");

        databaseService.updateVolunteering(v, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(CancelingVolunteers.this,
                        "ההתנדבות בוטלה בהצלחה", Toast.LENGTH_SHORT).show();
                loadVolunteering();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CancelingVolunteers.this,
                        "שגיאה בביטול ההתנדבות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private android.graphics.drawable.GradientDrawable createCircle(String color) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        d.setColor(Color.parseColor(color));
        return d;
    }

    private android.graphics.drawable.GradientDrawable createRoundedBackground(String color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(Color.parseColor(color));
        d.setCornerRadius(radius);
        return d;
    }
}