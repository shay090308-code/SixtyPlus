package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

public class ChangeDetailsInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserInChargefirstName, etUserInChargeLastName, etUserInChargeId, etUserInChargePhone, etUserInChargePassword,
            etUserInChargePlaceName, etUserInChargeAdress, etUserInChargeHours, etUserInChargeDays ,etUserInChargeNewPassword,
            etUserInChargeDescription, etUserInChargeNewPasswordConfirm, etUserInChargeCity;
    private Button btnUpdateProfile;
    String selectedUid;
    UserInCharge selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_details_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_details_student), (v, insets) -> {
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
        etUserInChargePlaceName = findViewById(R.id.placeNameChange);
        etUserInChargeAdress = findViewById(R.id.placeAdressChange);
        etUserInChargeDays = findViewById(R.id.placeDaysChange);
        etUserInChargeHours = findViewById(R.id.placeHoursChange);
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
                etUserInChargeDays.setText(selectedUser.getDaysAvailable());
                etUserInChargeHours.setText(selectedUser.getHrsAvailable());
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
        String days = etUserInChargeDays.getText().toString();
        String hours = etUserInChargeHours.getText().toString();
        String desc = etUserInChargeDescription.getText().toString() + "";
        String password = etUserInChargeNewPassword.getText().toString() + "";
        String confPass = etUserInChargeNewPasswordConfirm.getText().toString() + "";

        if (!isValid(firstName, lastName, phone, password, confPass)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // Update the user object
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setPhoneNumber(phone);
        selectedUser.setCity(city);
        selectedUser.setAdress(adress);
        selectedUser.setDaysAvailable(days);
        selectedUser.setHrsAvailable(hours);
        selectedUser.setPlaceName(placeName);
        selectedUser.setDesc(desc);
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