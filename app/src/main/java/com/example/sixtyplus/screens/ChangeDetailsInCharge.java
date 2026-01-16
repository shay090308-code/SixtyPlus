package com.example.sixtyplus.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.HourMinute;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.Weekday;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;
import com.example.sixtyplus.views.IntervalTimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeDetailsInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserInChargefirstName, etUserInChargeLastName, etUserInChargeId, etUserInChargePhone, etUserInChargePassword,
            etUserInChargePlaceName, etUserInChargeAdress, etUserInChargeNewPassword,
            etUserInChargeDescription, etUserInChargeNewPasswordConfirm;
    private Button btnUpdateProfile;
    private String selectedUid;
    private Spinner etUserInChargeCity;
    private UserInCharge selectedUser;
    private String[] regions;
    private EditText startSun, startMon, startTue, startWed, startThu, startFri, startSat, endSun, endMon,
            endTue, endWed, endThu, endFri, endSat;
    private EditText[] startDays;
    private EditText[] endDays;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_details_in_charge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_details_in_charge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserInCharge currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);
        assert currentUser != null;
        selectedUid = currentUser.getId();

        Log.d(TAG, "Selected user: " + selectedUid);

        // Initialize the EditText fields
        etUserInChargefirstName = findViewById(R.id.firstNameInChargeChange);
        etUserInChargeLastName = findViewById(R.id.lastNameInChargeChange);
        etUserInChargeId = findViewById(R.id.idNumberInChargeChange);
        etUserInChargePhone = findViewById(R.id.phoneInChargeChange);
        etUserInChargePassword = findViewById(R.id.nowPasswordInChargeChange);
        etUserInChargeNewPassword = findViewById(R.id.newPasswordInCharge);
        etUserInChargeNewPasswordConfirm = findViewById(R.id.newPasswordInChargeConfirm);
        etUserInChargeCity = findViewById(R.id.cityInChargeChange);
        regions = getResources().getStringArray(R.array.regions_array);
       adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_selected,
                android.R.id.text1,
                regions);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        etUserInChargeCity.setAdapter(adapter);
        etUserInChargePlaceName = findViewById(R.id.placeNameChange);
        etUserInChargeAdress = findViewById(R.id.placeAdressChange);
        etUserInChargeDescription = findViewById(R.id.placeDescription);
        btnUpdateProfile = findViewById(R.id.saveChangesInChargeBtn);
        startSun = findViewById(R.id.startSunday);
        startMon = findViewById(R.id.startMonday);
        startTue = findViewById(R.id.startTuesday);
        startWed = findViewById(R.id.startWednesday);
        startThu = findViewById(R.id.startThursday);
        startFri = findViewById(R.id.startFriday);
        startSat = findViewById(R.id.startSaturday);
        endSun = findViewById(R.id.endSunday);
        endMon = findViewById(R.id.endMonday);
        endTue = findViewById(R.id.endTuesday);
        endWed = findViewById(R.id.endWednesday);
        endThu = findViewById(R.id.endThursday);
        endFri = findViewById(R.id.endFriday);
        endSat = findViewById(R.id.endSaturday);

        startDays = new EditText[]{
                startSun,
                startMon,
                startTue,
                startWed,
                startThu,
                startFri,
                startSat
        };

        endDays = new EditText[]{
                endSun,
                endMon,
                endTue,
                endWed,
                endThu,
                endFri,
                endSat
        };
        for (EditText startDay : startDays) {
            setIntervalTimePicker(startDay);
        }

        for (EditText endDay : endDays) {
            setIntervalTimePicker(endDay);
        }


        Log.d(TAG, "Selected user: " + selectedUid);

        btnUpdateProfile.setOnClickListener(this);

        showUserProfile();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnUpdateProfile.getId()) {
            updateUserProfile();
            return;
        }
    }

    private void showUserProfile() {
        // Get the user data from database
        databaseService.getUserInCharge(selectedUid, new DatabaseService.DatabaseCallback<UserInCharge>() {
            @Override
            public void onCompleted(UserInCharge user) {
                selectedUser = user;
                SharedPreferencesUtils.saveUser(ChangeDetailsInCharge.this, user);
                // Set the user data to the EditText fields
                etUserInChargefirstName.setText(selectedUser.getFirstName());
                etUserInChargeLastName.setText(selectedUser.getLastName());
                etUserInChargeId.setText(selectedUser.getId());
                if (selectedUser.getCity() != null) {
                    String userCity = selectedUser.getCity();
                    int spinnerPosition = adapter.getPosition(userCity);

                    if (spinnerPosition != -1) {
                        etUserInChargeCity.setSelection(spinnerPosition);
                    }
                }
                etUserInChargeAdress.setText(selectedUser.getAdress());
                etUserInChargePlaceName.setText(selectedUser.getPlaceName());
                etUserInChargePhone.setText(selectedUser.getPhoneNumber());
                etUserInChargePassword.setText(selectedUser.getPassword() + "");
                etUserInChargeDescription.setText(selectedUser.getDesc() + "");
                startSun.setText(selectedUser.getDayAndHours(Weekday.SUNDAY).getStartTime().toString());
                startMon.setText(selectedUser.getDayAndHours(Weekday.MONDAY).getStartTime().toString());
                startTue.setText(selectedUser.getDayAndHours(Weekday.TUESDAY).getStartTime().toString());
                startWed.setText(selectedUser.getDayAndHours(Weekday.WEDNESDAY).getStartTime().toString());
                startThu.setText(selectedUser.getDayAndHours(Weekday.THURSDAY).getStartTime().toString());
                startFri.setText(selectedUser.getDayAndHours(Weekday.FRIDAY).getStartTime().toString());
                startSat.setText(selectedUser.getDayAndHours(Weekday.SATURDAY).getStartTime().toString());
                endSun.setText(selectedUser.getDayAndHours(Weekday.SUNDAY).getEndTime().toString());
                endMon.setText(selectedUser.getDayAndHours(Weekday.MONDAY).getEndTime().toString());
                endTue.setText(selectedUser.getDayAndHours(Weekday.TUESDAY).getEndTime().toString());
                endWed.setText(selectedUser.getDayAndHours(Weekday.WEDNESDAY).getEndTime().toString());
                endThu.setText(selectedUser.getDayAndHours(Weekday.THURSDAY).getEndTime().toString());
                endFri.setText(selectedUser.getDayAndHours(Weekday.FRIDAY).getEndTime().toString());
                endSat.setText(selectedUser.getDayAndHours(Weekday.SATURDAY).getEndTime().toString());
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Log.e(TAG, "User not found");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the updated user data from the EditText fields
        String firstName = etUserInChargefirstName.getText().toString();
        String lastName = etUserInChargeLastName.getText().toString();
        String phone = etUserInChargePhone.getText().toString();
        String city = etUserInChargeCity.getSelectedItem().toString();
        String adress = etUserInChargeAdress.getText().toString();
        String placeName = etUserInChargePlaceName.getText().toString();
        String desc;
        if (etUserInChargeDescription.length() != 0) {
            desc = etUserInChargeDescription.getText().toString();
        } else
            desc = " ";
        String password = etUserInChargeNewPassword.getText().toString() + "";
        String confPass = etUserInChargeNewPasswordConfirm.getText().toString() + "";

        if (!isValid(firstName, lastName, phone, password, confPass)) {
            Log.e(TAG, "Invalid input");
            return;
        }
        
        for (Weekday weekday : Weekday.values()) {
            String start = startDays[weekday.ordinal()].getText().toString();
            String end = endDays[weekday.ordinal()].getText().toString();
            if (!start.isEmpty() && !end.isEmpty()) {
                selectedUser.setDayAndHours(new DayAndHours(weekday, HourMinute.fromString(start), HourMinute.fromString(end)));
            }
        }

        // Update the user object
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setPhoneNumber(phone);
        selectedUser.setCity(city);
        selectedUser.setAdress(adress);
        selectedUser.setPlaceName(placeName);
        selectedUser.setDesc(desc);
        if (!password.isEmpty())
            selectedUser.setPassword(password);

        // Update the user data in the authentication
        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "User password: " + selectedUser.getPassword());


        updateUserInDatabase(selectedUser);
    }



    private void updateUserInDatabase(UserInCharge user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(ChangeDetailsInCharge.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                showUserProfile(); // Refresh the profile view
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(ChangeDetailsInCharge.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String fName, String lName, String phone, String password,String confirmPass) {

        if (!validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            /// show error message to user
            Toast.makeText(this, "על הסיסמא להיות בעלת 6 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isNameValid(fName)) {
            Log.e(TAG, "checkInput: First name must be at least 2 characters long");
            /// show error message to user
            Toast.makeText(this, "על השם להיות בעל 2 תויים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isNameValid(lName)) {
            Log.e(TAG, "checkInput: Last name must be at least 2 characters long");
            /// show error message to user
            Toast.makeText(this, "על השם להיות בעל 2 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isPhoneValid(phone)) {
            Log.e(TAG, "checkInput: Phone number must be at least 10 characters long");
            Toast.makeText(this, "על מספר הטלפון להיות בעל 10 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!validator.isConfirmPasswordValid(password, confirmPass)) {
            Log.e(TAG, "checkInput: Passwords do not match");
            Toast.makeText(this, "הסיסמאות לא זהות", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    void setIntervalTimePicker(EditText timeEditText){
        timeEditText.setFocusable(false); // Prevent keyboard from showing
        timeEditText.setClickable(true);

        timeEditText.setText("00:00");

        timeEditText.setOnClickListener(v -> {
            // Parse current time from EditText
            String currentTime = timeEditText.getText().toString();
            int currentHour = 0;
            int currentMinute = 0;

            if (!currentTime.isEmpty() && currentTime.contains(":")) {
                String[] parts = currentTime.split(":");
                currentHour = Integer.parseInt(parts[0]);
                currentMinute = Integer.parseInt(parts[1]);
            }

            // Create a dialog with your IntervalTimePicker
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.time_picker_dialog, null);

            IntervalTimePicker timePicker = dialogView.findViewById(R.id.intervalTimePicker);

            // Set the current time to the picker
            timePicker.setHour(currentHour);
            timePicker.setMinute(currentMinute);

            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String time = String.format("%02d:%02d", hour, minute);
                        timeEditText.setText(time);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}