package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeDetailsInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserInChargefirstName, etUserInChargeLastName, etUserInChargeId, etUserInChargePhone, etUserInChargePassword,
            etUserInChargePlaceName, etUserInChargeAdress, etUserInChargeNewPassword,
            etUserInChargeDescription, etUserInChargeNewPasswordConfirm, etUserInChargeCity;
    private Button btnUpdateProfile;
    String selectedUid;
    UserInCharge selectedUser;
    // הוסף למעלה עם שאר ה-EditText
    Spinner sunSH, sunSM, sunEH, sunEM;
    Spinner monSH, monSM, monEH, monEM;
    Spinner tueSH, tueSM, tueEH, tueEM;
    Spinner wedSH, wedSM, wedEH, wedEM;
    Spinner thuSH, thuSM, thuEH, thuEM;
    Spinner friSH, friSM, friEH, friEM;
    Spinner satSH, satSM, satEH, satEM;

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

        initViews();
        fillSpinners();

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
        etUserInChargePlaceName = findViewById(R.id.placeNameChange);
        etUserInChargeAdress = findViewById(R.id.placeAdressChange);
        etUserInChargeDescription = findViewById(R.id.placeDescription);
        btnUpdateProfile = findViewById(R.id.saveChangesInChargeBtn);


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
                etUserInChargeCity.setText(selectedUser.getCity());
                etUserInChargeAdress.setText(selectedUser.getAdress());
                etUserInChargePlaceName.setText(selectedUser.getPlaceName());
                etUserInChargePhone.setText(selectedUser.getPhoneNumber());
                etUserInChargePassword.setText(selectedUser.getPassword() + "");
                etUserInChargeDescription.setText(selectedUser.getDesc() + "");
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
        String city = etUserInChargeCity.getText().toString();
        String adress = etUserInChargeAdress.getText().toString();
        String placeName = etUserInChargePlaceName.getText().toString();
        String desc = etUserInChargeDescription.getText().toString() + "";
        String password = etUserInChargeNewPassword.getText().toString() + "";
        String confPass = etUserInChargeNewPasswordConfirm.getText().toString() + "";

        if (!isValid(firstName, lastName, phone, password, confPass)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        List<DayAndHours> updatedSchedule = new ArrayList<>();
        updatedSchedule.add(getDayData("ראשון", sunSH, sunSM, sunEH, sunEM));
        updatedSchedule.add(getDayData("שני", monSH, monSM, monEH, monEM));
        updatedSchedule.add(getDayData("שלישי", tueSH, tueSM, tueEH, tueEM));
        updatedSchedule.add(getDayData("רביעי", wedSH, wedSM, wedEH, wedEM));
        updatedSchedule.add(getDayData("חמישי", thuSH, thuSM, thuEH, thuEM));
        updatedSchedule.add(getDayData("שישי", friSH, friSM, friEH, friEM));
        updatedSchedule.add(getDayData("שבת", satSH, satSM, satEH, satEM));

        // Update the user object
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setPhoneNumber(phone);
        selectedUser.setCity(city);
        selectedUser.setAdress(adress);
        selectedUser.setPlaceName(placeName);
        selectedUser.setDesc(desc);
        selectedUser.setPassword(password);
        selectedUser.setSchedule(updatedSchedule);

        // Update the user data in the authentication
        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "User password: " + selectedUser.getPassword());

        updateUserInDatabase(selectedUser);
    }

    private void loadUserSchedule(List<DayAndHours> schedule) {
        if (schedule == null) return;
        Spinner[][] daySpinners = {
                {sunSH, sunSM, sunEH, sunEM}, {monSH, monSM, monEH, monEM},
                {tueSH, tueSM, tueEH, tueEM}, {wedSH, wedSM, wedEH, wedEM},
                {thuSH, thuSM, thuEH, thuEM}, {friSH, friSM, friEH, friEM},
                {satSH, satSM, satEH, satEM}
        };

        for (int i = 0; i < schedule.size() && i < 7; i++) {
            DayAndHours day = schedule.get(i);
            if (day.getStartTime() != null && day.getEndTime() != null) {
                String[] start = day.getStartTime().split(":");
                String[] end = day.getEndTime().split(":");
                setSpinnerValue(daySpinners[i][0], start[0]);
                setSpinnerValue(daySpinners[i][1], start[1]);
                setSpinnerValue(daySpinners[i][2], end[0]);
                setSpinnerValue(daySpinners[i][3], end[1]);
            }
        }
    }
    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) spinner.setSelection(position);
        }
    }

    public DayAndHours getDayData(String dayName, Spinner hStart, Spinner mStart, Spinner hEnd, Spinner mEnd) {
        DayAndHours dayObj = new DayAndHours();
        dayObj.day = dayName;
        String sH = hStart.getSelectedItem().toString();
        String sM = mStart.getSelectedItem().toString();
        String eH = hEnd.getSelectedItem().toString();
        String eM = mEnd.getSelectedItem().toString();

        if (sH.equals("שעה") && sM.equals("דקה")) {
            dayObj.setStartTime(null);
            dayObj.setEndTime(null);
            dayObj.remark = "Closed this day";
        } else {
            dayObj.setStartTime(sH + ":" + sM);
            dayObj.setEndTime(eH + ":" + eM);
            dayObj.remark = null;
        }
        return dayObj;
    }

    private void initViews() {
        sunSH = findViewById(R.id.spinner_sun_start_hourChange);
        sunSM = findViewById(R.id.spinner_sun_start_minChange);
        sunEH = findViewById(R.id.spinner_sun_end_hourChange);
        sunEM = findViewById(R.id.spinner_sun_end_minChange);
        monSH = findViewById(R.id.spinner_mon_start_hourChange);
        monSM = findViewById(R.id.spinner_mon_start_minChange);
        monEH = findViewById(R.id.spinner_mon_end_hourChange);
        monEM = findViewById(R.id.spinner_mon_end_minChange);
        tueSH = findViewById(R.id.spinner_tue_start_hourChange);
        tueSM = findViewById(R.id.spinner_tue_start_minChange);
        tueEH = findViewById(R.id.spinner_tue_end_hourChange);
        tueEM = findViewById(R.id.spinner_tue_end_minChange);
        wedSH = findViewById(R.id.spinner_wed_start_hourChange);
        wedSM = findViewById(R.id.spinner_wed_start_minChange);
        wedEH = findViewById(R.id.spinner_wed_end_hourChange);
        wedEM = findViewById(R.id.spinner_wed_end_minChange);
        thuSH = findViewById(R.id.spinner_thu_start_hourChange);
        thuSM = findViewById(R.id.spinner_thu_start_minChange);
        thuEH = findViewById(R.id.spinner_thu_end_hourChange);
        thuEM = findViewById(R.id.spinner_thu_end_minChange);
        friSH = findViewById(R.id.spinner_fri_start_hourChange);
        friSM = findViewById(R.id.spinner_fri_start_minChange);
        friEH = findViewById(R.id.spinner_fri_end_hourChange);
        friEM = findViewById(R.id.spinner_fri_end_minChange);
        satSH = findViewById(R.id.spinner_sat_start_hourChange);
        satSM = findViewById(R.id.spinner_sat_start_minChange);
        satEH = findViewById(R.id.spinner_sat_end_hourChange);
        satEM = findViewById(R.id.spinner_sat_end_minChange);
    }
    private void fillSpinners() {
        List<String> hours = new ArrayList<>();
        hours.add("שעה");
        for (int i = 0; i < 24; i++) hours.add(String.format("%02d", i));
        String[] mins = {"דקה", "00", "15", "30", "45"};

        ArrayAdapter<String> hAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(mins));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner[] hSpinners = {sunSH, sunEH, monSH, monEH, tueSH, tueEH, wedSH, wedEH, thuSH, thuEH, friSH, friEH, satSH, satEH};
        Spinner[] mSpinners = {sunSM, sunEM, monSM, monEM, tueSM, tueEM, wedSM, wedEM, thuSM, thuEM, friSM, friEM, satSM, satEM};

        for (Spinner s : hSpinners) s.setAdapter(hAdapter);
        for (Spinner s : mSpinners) s.setAdapter(mAdapter);
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

    private boolean isValid(String firstName, String lastName, String phone, String password,String confirmPass) {
        if (!validator.isNameValid(firstName)) {
            etUserInChargefirstName.setError("First name is required");
            etUserInChargefirstName.requestFocus();
            return false;
        }
        if (!validator.isNameValid(lastName)) {
            etUserInChargeLastName.setError("Last name is required");
            etUserInChargeLastName.requestFocus();
            return false;
        }
        if (!validator.isPhoneValid(phone)) {
            etUserInChargePhone.setError("Phone number is required");
            etUserInChargePhone.requestFocus();
            return false;
        }
        if(!password.equals(confirmPass)) {
            etUserInChargeNewPassword.setError("Passwords are not similar");
            etUserInChargeNewPassword.requestFocus();
            return false;
        }

        return true;
    }
}