package com.example.sixtyplus.screens;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.HourMinute;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.models.Weekday;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.ImageUtil;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.views.IntervalTimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisterVolunteering extends BaseActivity {

    private static final String TAG = "RegisterVolunteering";

    private TextView tvStudentName, tvSelectedPlace, tvPlaceSchedule, tvVolunteeringDuration;
    private AutoCompleteTextView actvSearchPlace;
    private Button btnSelectDate, btnSubmitVolunteering, btnSelectStartTime, btnSelectEndTime;
    private CardView cardPlaceImages, cardPlaceSchedule, cardDateTime;
    private LinearLayout imagesContainer;
    private FrameLayout imageOverlay;
    private ImageView ivFullscreenImage;
    private TextView btnCloseOverlay;

    private UserStudent currentStudent;
    private List<UserInCharge> availablePlaces;
    private Map<String, UserInCharge> placesMap;
    private UserInCharge selectedPlace;

    private Calendar selectedDate;
    private DayAndHours placeScheduleForSelectedDay;
    private HourMinute selectedStartTime;
    private HourMinute selectedEndTime;

    private String preSelectedPlaceId;
    private String preSelectedPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_volunteering);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        preSelectedPlaceId = getIntent().getStringExtra("selectedPlaceId");
        preSelectedPlaceName = getIntent().getStringExtra("selectedPlaceName");

        initializeViews();
        initializeData();
        loadAvailablePlaces();
        setupListeners();
    }

    private void initializeViews() {
        tvStudentName = findViewById(R.id.tvStudentName);
        tvSelectedPlace = findViewById(R.id.tvSelectedPlace);
        tvPlaceSchedule = findViewById(R.id.tvPlaceSchedule);
        tvVolunteeringDuration = findViewById(R.id.tvVolunteeringDuration);
        actvSearchPlace = findViewById(R.id.actvSearchPlace);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSubmitVolunteering = findViewById(R.id.btnSubmitVolunteering);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        cardPlaceImages = findViewById(R.id.cardPlaceImages);
        cardPlaceSchedule = findViewById(R.id.cardPlaceSchedule);
        cardDateTime = findViewById(R.id.cardDateTime);
        imagesContainer = findViewById(R.id.imagesContainer);
        imageOverlay = findViewById(R.id.imageOverlay);
        ivFullscreenImage = findViewById(R.id.ivFullscreenImage);
        btnCloseOverlay = findViewById(R.id.btnCloseOverlay);
    }

    private void initializeData() {
        availablePlaces = new ArrayList<>();
        placesMap = new HashMap<>();

        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);
        if (currentStudent != null) {
            tvStudentName.setText(currentStudent.getFirstName() + " " + currentStudent.getLastName());
        }
    }

    private void loadAvailablePlaces() {
        if (currentStudent == null) {
            Toast.makeText(this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseService.getInstance().getUserInChargeList(new DatabaseService.DatabaseCallback<List<UserInCharge>>() {
            @Override
            public void onCompleted(List<UserInCharge> userInCharges) {
                availablePlaces.clear();
                placesMap.clear();

                for (UserInCharge userInCharge : userInCharges) {
                    if (userInCharge.isAccepted() &&
                            userInCharge.className != null &&
                            userInCharge.className.equals(UserInCharge.class.getName()) &&
                            userInCharge.getCity() != null &&
                            userInCharge.getCity().equals(currentStudent.getCity())) {
                        availablePlaces.add(userInCharge);
                        placesMap.put(userInCharge.getPlaceName(), userInCharge);
                    }
                }
                setupAutoComplete();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterVolunteering.this, "שגיאה בטעינת מקומות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutoComplete() {
        List<String> placeNames = new ArrayList<>();
        for (UserInCharge place : availablePlaces) {
            placeNames.add(place.getPlaceName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, placeNames);
        actvSearchPlace.setAdapter(adapter);
        actvSearchPlace.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlaceName = (String) parent.getItemAtPosition(position);
            onPlaceSelected(selectedPlaceName);
        });

        if (preSelectedPlaceName != null && !preSelectedPlaceName.isEmpty()) {
            actvSearchPlace.setText(preSelectedPlaceName);
            onPlaceSelected(preSelectedPlaceName);
        }
    }

    private void onPlaceSelected(String placeName) {
        selectedPlace = placesMap.get(placeName);
        if (selectedPlace == null) return;

        if (isPlaceClosedAllWeek()) {
            tvPlaceSchedule.setText("מקום זה סגור בכל ימות השבוע");
            tvPlaceSchedule.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            cardPlaceSchedule.setVisibility(View.VISIBLE);
            cardPlaceImages.setVisibility(View.GONE);
            cardDateTime.setVisibility(View.GONE);
            btnSelectDate.setEnabled(false);
            btnSelectStartTime.setEnabled(false);
            btnSelectEndTime.setEnabled(false);
            btnSubmitVolunteering.setEnabled(false);
            return;
        }

        tvSelectedPlace.setText("נבחר: " + placeName);
        tvSelectedPlace.setVisibility(View.VISIBLE);

        // הצגת תמונות המקום
        displayPlaceImages();

        // הצגת שעות
        displayPlaceSchedule();
        cardPlaceSchedule.setVisibility(View.VISIBLE);
        cardDateTime.setVisibility(View.VISIBLE);

        btnSelectDate.setEnabled(true);
        selectedDate = null;
        selectedStartTime = null;
        selectedEndTime = null;
        btnSelectDate.setText("בחר תאריך");
        btnSelectStartTime.setText("בחר שעה");
        btnSelectStartTime.setEnabled(false);
        btnSelectEndTime.setText("בחר שעה");
        btnSelectEndTime.setEnabled(false);
        tvVolunteeringDuration.setVisibility(View.GONE);
        updateSubmitButton();
    }

    private void displayPlaceImages() {
        imagesContainer.removeAllViews();
        List<String> images = selectedPlace.getImages();

        if (images == null || images.isEmpty()) {
            cardPlaceImages.setVisibility(View.GONE);
            return;
        }

        cardPlaceImages.setVisibility(View.VISIBLE);
        int sizePx = (int) (110 * getResources().getDisplayMetrics().density);
        int marginPx = (int) (6 * getResources().getDisplayMetrics().density);

        for (String base64 : images) {
            Bitmap bmp = ImageUtil.fromBase64(base64);
            if (bmp == null) continue;

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            params.setMargins(marginPx, 0, marginPx, 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bmp);
            imageView.setClipToOutline(true);

            android.graphics.drawable.GradientDrawable rounded =
                    new android.graphics.drawable.GradientDrawable();
            rounded.setCornerRadius(20f);
            rounded.setColor(android.graphics.Color.LTGRAY);
            imageView.setBackground(rounded);

            // לחיצה לתמונה בגדול
            final Bitmap finalBmp = bmp;
            imageView.setOnClickListener(v -> showFullscreenImage(finalBmp));

            imagesContainer.addView(imageView);
        }
    }

    private void showFullscreenImage(Bitmap bmp) {
        ivFullscreenImage.setImageBitmap(bmp);
        imageOverlay.setVisibility(View.VISIBLE);
    }

    private void displayPlaceSchedule() {
        StringBuilder scheduleText = new StringBuilder();
        Weekday[] weekdays = {Weekday.SUNDAY, Weekday.MONDAY, Weekday.TUESDAY,
                Weekday.WEDNESDAY, Weekday.THURSDAY, Weekday.FRIDAY, Weekday.SATURDAY};
        String[] hebrewDays = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};

        for (int i = 0; i < weekdays.length; i++) {
            DayAndHours daySchedule = selectedPlace.getDayAndHours(weekdays[i]);
            if (daySchedule.checkIfClosed()) {
                scheduleText.append("יום ").append(hebrewDays[i]).append(": סגור\n");
            } else {
                scheduleText.append("יום ").append(hebrewDays[i]).append(": ")
                        .append(daySchedule.getStartTime().toString())
                        .append(" - ")
                        .append(daySchedule.getEndTime().toString())
                        .append("\n");
            }
        }

        tvPlaceSchedule.setText(scheduleText.toString().trim());
        tvPlaceSchedule.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSubmitVolunteering.setOnClickListener(v -> submitVolunteering());
        btnSelectStartTime.setOnClickListener(v -> showStartTimePicker());
        btnSelectEndTime.setOnClickListener(v -> showEndTimePicker());
        imageOverlay.setOnClickListener(v -> imageOverlay.setVisibility(View.GONE));
        btnCloseOverlay.setOnClickListener(v -> imageOverlay.setVisibility(View.GONE));
    }

    private boolean isPlaceClosedAllWeek() {
        if (selectedPlace == null) return true;
        for (Weekday day : Weekday.values()) {
            if (!selectedPlace.getDayAndHours(day).checkIfClosed()) return false;
        }
        return true;
    }

    private void showDatePicker() {
        if (selectedPlace == null) {
            Toast.makeText(this, "אנא בחר מקום התנדבות תחילה", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    onDateSelected();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void onDateSelected() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnSelectDate.setText(sdf.format(selectedDate.getTime()));

        Weekday weekday = getWeekdayFromCalendar(selectedDate);
        placeScheduleForSelectedDay = selectedPlace.getDayAndHours(weekday);

        if (placeScheduleForSelectedDay.checkIfClosed()) {
            Toast.makeText(this, "המקום סגור ביום זה", Toast.LENGTH_LONG).show();
            btnSelectStartTime.setEnabled(false);
            btnSelectEndTime.setEnabled(false);
            selectedStartTime = null;
            selectedEndTime = null;
            btnSelectStartTime.setText("בחר שעה");
            btnSelectEndTime.setText("בחר שעה");
            updateSubmitButton();
            return;
        }

        btnSelectStartTime.setEnabled(true);
        btnSelectEndTime.setEnabled(true);
        selectedStartTime = null;
        selectedEndTime = null;
        btnSelectStartTime.setText("בחר שעה");
        btnSelectEndTime.setText("בחר שעה");
        updateSubmitButton();
    }

    private Weekday getWeekdayFromCalendar(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY: return Weekday.SUNDAY;
            case Calendar.MONDAY: return Weekday.MONDAY;
            case Calendar.TUESDAY: return Weekday.TUESDAY;
            case Calendar.WEDNESDAY: return Weekday.WEDNESDAY;
            case Calendar.THURSDAY: return Weekday.THURSDAY;
            case Calendar.FRIDAY: return Weekday.FRIDAY;
            case Calendar.SATURDAY: return Weekday.SATURDAY;
            default: return Weekday.SUNDAY;
        }
    }

    private void showStartTimePicker() {
        if (placeScheduleForSelectedDay == null || placeScheduleForSelectedDay.checkIfClosed()) {
            Toast.makeText(this, "אנא בחר תאריך תחילה", Toast.LENGTH_SHORT).show();
            return;
        }
        showTimePickerDialog(true);
    }

    private void showEndTimePicker() {
        if (selectedStartTime == null) {
            Toast.makeText(this, "אנא בחר שעת התחלה תחילה", Toast.LENGTH_SHORT).show();
            return;
        }
        showTimePickerDialog(false);
    }

    private void showTimePickerDialog(boolean isStartTime) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.time_picker_dialog, null);
        IntervalTimePicker timePicker = dialogView.findViewById(R.id.intervalTimePicker);

        HourMinute openTime = placeScheduleForSelectedDay.getStartTime();
        HourMinute closeTime = placeScheduleForSelectedDay.getEndTime();

        if (isStartTime) {
            timePicker.setHour(openTime.getHour());
            timePicker.setMinute(openTime.getMinute());
        } else {
            timePicker.setHour(selectedStartTime.getHour());
            timePicker.setMinute(selectedStartTime.getMinute());
        }

        new AlertDialog.Builder(this)
                .setTitle(isStartTime ? "בחר שעת התחלה" : "בחר שעת סיום")
                .setView(dialogView)
                .setPositiveButton("אישור", (dialog, i) -> {
                    HourMinute selectedTime = new HourMinute(
                            timePicker.getHour(), timePicker.getMinute());

                    if (selectedTime.compareTo(openTime) < 0 || selectedTime.compareTo(closeTime) > 0) {
                        Toast.makeText(this, "השעה חייבת להיות בטווח שעות הפעילות", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isStartTime) {
                        if (selectedEndTime != null && selectedTime.compareTo(selectedEndTime) >= 0) {
                            Toast.makeText(this, "שעת ההתחלה חייבת להיות לפני שעת הסיום", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedStartTime = selectedTime;
                        btnSelectStartTime.setText(selectedTime.toString());
                    } else {
                        if (selectedTime.compareTo(selectedStartTime) <= 0) {
                            Toast.makeText(this, "שעת הסיום חייבת להיות אחרי שעת ההתחלה", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedEndTime = selectedTime;
                        btnSelectEndTime.setText(selectedTime.toString());
                    }

                    updateDuration();
                    updateSubmitButton();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void updateDuration() {
        Volunteering v = new Volunteering();
        v.setStartTime(selectedStartTime);
        v.setEndTime(selectedEndTime);
        double hours = v.getCalculateTotalHours();

        if (hours <= 0) {
            tvVolunteeringDuration.setText("שעת הסיום חייבת להיות אחרי שעת ההתחלה");
            tvVolunteeringDuration.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvVolunteeringDuration.setText(String.format("משך ההתנדבות: %.1f שעות", hours));
            tvVolunteeringDuration.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        tvVolunteeringDuration.setVisibility(View.VISIBLE);
    }

    private void updateSubmitButton() {
        boolean isValid = selectedPlace != null &&
                selectedDate != null &&
                placeScheduleForSelectedDay != null &&
                !placeScheduleForSelectedDay.checkIfClosed() &&
                selectedStartTime != null &&
                selectedEndTime != null &&
                selectedEndTime.compareTo(selectedStartTime) > 0;
        btnSubmitVolunteering.setEnabled(isValid);
    }

    private boolean isValidTimeRange() {
        if (selectedStartTime == null || selectedEndTime == null || placeScheduleForSelectedDay == null)
            return false;
        HourMinute openTime = placeScheduleForSelectedDay.getStartTime();
        HourMinute closeTime = placeScheduleForSelectedDay.getEndTime();
        return selectedEndTime.compareTo(selectedStartTime) > 0 &&
                selectedStartTime.compareTo(openTime) >= 0 &&
                selectedEndTime.compareTo(closeTime) <= 0;
    }

    private void submitVolunteering() {
        if (!isValidTimeRange()) {
            Toast.makeText(this, "שעות לא תקינות", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitVolunteering.setEnabled(false);

        databaseService.getVolunteeringByStudent(currentStudent.getId(),
                new DatabaseService.DatabaseCallback<List<Volunteering>>() {
                    @Override
                    public void onCompleted(List<Volunteering> existingList) {
                        for (Volunteering existing : existingList) {
                            if ("rejected".equals(existing.getStatus())) continue;

                            Calendar existingCal = Calendar.getInstance();
                            existingCal.setTimeInMillis(existing.getDateMillis());
                            boolean sameDay =
                                    existingCal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                            existingCal.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR);

                            if (!sameDay) continue;

                            int newStart = selectedStartTime.getHour() * 60 + selectedStartTime.getMinute();
                            int newEnd = selectedEndTime.getHour() * 60 + selectedEndTime.getMinute();
                            int exStart = existing.getStartTime().getHour() * 60 + existing.getStartTime().getMinute();
                            int exEnd = existing.getEndTime().getHour() * 60 + existing.getEndTime().getMinute();

                            boolean overlaps = newStart < exEnd && newEnd > exStart;

                            if (overlaps) {
                                runOnUiThread(() -> {
                                    btnSubmitVolunteering.setEnabled(true);
                                    Toast.makeText(RegisterVolunteering.this,
                                            "קיימת כבר התנדבות בשעות " +
                                                    existing.getStartTime().toString() + "-" +
                                                    existing.getEndTime().toString() +
                                                    " באותו יום",
                                            Toast.LENGTH_LONG).show();
                                });
                                return;
                            }
                        }

                        Volunteering volunteering = new Volunteering(
                                currentStudent.getId(),
                                currentStudent.getFirstName() + " " + currentStudent.getLastName(),
                                selectedPlace.getId(),
                                selectedPlace.getPlaceName(),
                                selectedDate.getTimeInMillis(),
                                selectedStartTime,
                                selectedEndTime
                        );

                        databaseService.setVolunteering(volunteering,
                                new DatabaseService.DatabaseCallback<Void>() {
                                    @Override
                                    public void onCompleted(Void v) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(RegisterVolunteering.this,
                                                    "בקשת ההתנדבות נשלחה בהצלחה!",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        });
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        runOnUiThread(() -> {
                                            btnSubmitVolunteering.setEnabled(true);
                                            Toast.makeText(RegisterVolunteering.this,
                                                    "שגיאה בשליחת הבקשה",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        runOnUiThread(() -> {
                            btnSubmitVolunteering.setEnabled(true);
                            Toast.makeText(RegisterVolunteering.this,
                                    "שגיאה בבדיקת התנדבויות קיימות",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }}