package com.example.sixtyplus.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.HourMinute;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.models.Weekday;
import com.example.sixtyplus.services.DatabaseService;
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

        // קבלת המקום שנבחר מהמסך הקודם (אם יש)
        preSelectedPlaceId = getIntent().getStringExtra("selectedPlaceId");
        preSelectedPlaceName = getIntent().getStringExtra("selectedPlaceName");

        // אתחול
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
    }

    private void initializeData() {
        availablePlaces = new ArrayList<>();
        placesMap = new HashMap<>();

        // טעינת פרטי התלמיד
        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);
        if (currentStudent != null) {
            String fullName = currentStudent.getFirstName() + " " + currentStudent.getLastName();
            tvStudentName.setText(fullName);
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
                    // סינון לפי: מאושר, מסוג UserInCharge, ובאותה עיר של התלמיד
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
                Log.e(TAG, "Failed to load places: " + e.getMessage());
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
                this,
                android.R.layout.simple_dropdown_item_1line,
                placeNames
        );

        actvSearchPlace.setAdapter(adapter);
        actvSearchPlace.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlaceName = (String) parent.getItemAtPosition(position);
            onPlaceSelected(selectedPlaceName);
        });

        // אם יש מקום שנבחר מראש, בחר אותו אוטומטית
        if (preSelectedPlaceName != null && !preSelectedPlaceName.isEmpty()) {
            actvSearchPlace.setText(preSelectedPlaceName);
            onPlaceSelected(preSelectedPlaceName);
        }
    }

    private void onPlaceSelected(String placeName) {
        selectedPlace = placesMap.get(placeName);
        if (selectedPlace != null) {
            // בדיקה אם המקום סגור בכל ימות השבוע
            if (isPlaceClosedAllWeek()) {

                tvPlaceSchedule.setText("מקום זה סגור בכל ימות השבוע");
                tvPlaceSchedule.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvPlaceSchedule.setVisibility(View.VISIBLE);

                // חסימת כל הכפתורים
                btnSelectDate.setEnabled(false);
                btnSelectStartTime.setEnabled(false);
                btnSelectEndTime.setEnabled(false);
                btnSubmitVolunteering.setEnabled(false);

                // איפוס ערכים
                selectedDate = null;
                selectedStartTime = null;
                selectedEndTime = null;
                btnSelectDate.setText("בחר תאריך");
                btnSelectStartTime.setText("בחר שעה");
                btnSelectEndTime.setText("בחר שעה");
                tvVolunteeringDuration.setText("");

                return;
            }

            // המקום פתוח לפחות ביום אחד - המשך רגיל
            tvSelectedPlace.setText("נבחר: " + placeName);
            tvSelectedPlace.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvSelectedPlace.setVisibility(View.VISIBLE);

            // הצגת שעות פתיחה של המקום
            displayPlaceSchedule();

            // אפשור כפתור בחירת תאריך
            btnSelectDate.setEnabled(true);

            // איפוס תאריך ושעות
            selectedDate = null;
            selectedStartTime = null;
            selectedEndTime = null;
            btnSelectDate.setText("בחר תאריך");
            btnSelectStartTime.setText("בחר שעה");
            btnSelectStartTime.setEnabled(false);
            btnSelectEndTime.setText("בחר שעה");
            btnSelectEndTime.setEnabled(false);
            tvVolunteeringDuration.setText("");
            updateSubmitButton();
        }
    }

    private boolean isPlaceClosedAllWeek() {
        if (selectedPlace == null) {
            return true;
        }

        Weekday[] weekdays = {
                Weekday.SUNDAY,
                Weekday.MONDAY,
                Weekday.TUESDAY,
                Weekday.WEDNESDAY,
                Weekday.THURSDAY,
                Weekday.FRIDAY,
                Weekday.SATURDAY
        };

        // בדיקה אם יש לפחות יום אחד שהמקום פתוח
        for (Weekday day : weekdays) {
            DayAndHours daySchedule = selectedPlace.getDayAndHours(day);
            if (!daySchedule.checkIfClosed()) {
                return false; // המקום פתוח ביום זה
            }
        }

        return true; // המקום סגור בכל הימים
    }

    private void displayPlaceSchedule() {
        StringBuilder scheduleText = new StringBuilder("שעות פתיחה:\n");

        // סדר הימים מיום ראשון
        Weekday[] weekdays = {
                Weekday.SUNDAY,
                Weekday.MONDAY,
                Weekday.TUESDAY,
                Weekday.WEDNESDAY,
                Weekday.THURSDAY,
                Weekday.FRIDAY,
                Weekday.SATURDAY
        };

        // שמות הימים בעברית
        String[] hebrewDays = {
                "ראשון",
                "שני",
                "שלישי",
                "רביעי",
                "חמישי",
                "שישי",
                "שבת"
        };

        for (int i = 0; i < weekdays.length; i++) {
            DayAndHours daySchedule = selectedPlace.getDayAndHours(weekdays[i]);

            String dayName = hebrewDays[i];
            if (daySchedule.checkIfClosed()) {
                scheduleText.append(dayName).append(": סגור\n");
            } else {
                String openTime = daySchedule.getStartTime().toString();
                String closeTime = daySchedule.getEndTime().toString();
                scheduleText.append(dayName).append(": ").append(openTime).append(" - ").append(closeTime).append("\n");
            }
        }

        tvPlaceSchedule.setText(scheduleText.toString().trim());
        tvPlaceSchedule.setTextColor(getResources().getColor(android.R.color.black));
        tvPlaceSchedule.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSubmitVolunteering.setOnClickListener(v -> submitVolunteering());
        btnSelectStartTime.setOnClickListener(v -> showStartTimePicker());
        btnSelectEndTime.setOnClickListener(v -> showEndTimePicker());
    }

    private void showDatePicker() {
        if (selectedPlace == null) {
            Toast.makeText(this, "אנא בחר מקום התנדבות תחילה", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7); // עד שבוע קדימה

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

        // הגבלת טווח תאריכים
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void onDateSelected() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnSelectDate.setText(sdf.format(selectedDate.getTime()));

        // קביעת יום בשבוע
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

        // אפשר בחירת שעות
        btnSelectStartTime.setEnabled(true);
        btnSelectEndTime.setEnabled(true);
        selectedStartTime = null;
        selectedEndTime = null;
        btnSelectStartTime.setText("בחר שעה");
        btnSelectEndTime.setText("בחר שעה");
        updateSubmitButton();
    }

    private Weekday getWeekdayFromCalendar(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
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

        // הגדרת שעה התחלתית
        if (isStartTime) {
            timePicker.setHour(openTime.getHour());
            timePicker.setMinute(openTime.getMinute());
        } else {
            timePicker.setHour(selectedStartTime.getHour());
            timePicker.setMinute(selectedStartTime.getMinute());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isStartTime ? "בחר שעת התחלה" : "בחר שעת סיום")
                .setView(dialogView)
                .setPositiveButton("אישור", (dialogInterface, i) -> {
                    int selectedHour = timePicker.getHour();
                    int selectedMinute = timePicker.getMinute();
                    HourMinute selectedTime = new HourMinute(selectedHour, selectedMinute);

                    // בדיקה שהשעה בטווח המותר
                    if (selectedTime.compareTo(openTime) < 0 || selectedTime.compareTo(closeTime) > 0) {
                        Toast.makeText(this, "השעה חייבת להיות בטווח שעות הפעילות", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isStartTime) {
                        // בדיקה ששעת ההתחלה לפני שעת הסיום אם כבר נבחרה
                        if (selectedEndTime != null && selectedTime.compareTo(selectedEndTime) >= 0) {
                            Toast.makeText(this, "שעת ההתחלה חייבת להיות לפני שעת הסיום", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedStartTime = selectedTime;
                        btnSelectStartTime.setText(selectedTime.toString());
                    } else {
                        // בדיקה ששעת הסיום אחרי שעת ההתחלה
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
                .create();

        dialog.show();
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
        if (selectedStartTime == null || selectedEndTime == null || placeScheduleForSelectedDay == null) {
            return false;
        }

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

        String studentName = currentStudent.getFirstName() + " " + currentStudent.getLastName();

        Volunteering volunteering = new Volunteering(
                currentStudent.getId(),
                studentName,
                selectedPlace.getId(),
                selectedPlace.getPlaceName(),
                selectedDate.getTimeInMillis(),
                selectedStartTime,
                selectedEndTime
        );

        DatabaseService.getInstance().setVolunteering(volunteering, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(RegisterVolunteering.this, "בקשת ההתנדבות נשלחה בהצלחה!", Toast.LENGTH_LONG).show();
                finish(); // חזרה למסך הקודם
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to save volunteering", e);
                Toast.makeText(RegisterVolunteering.this, "שגיאה בשליחת הבקשה", Toast.LENGTH_SHORT).show();
            }
        });

    }
}