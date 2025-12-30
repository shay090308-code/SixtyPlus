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
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

public class ChangeDetailsStudent extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserStudentFirstName, etUserStudentLastName, etUserStudentId, etUserStudentPhone, etUserStudentPassword,
            etUserStudentSchool, etUserStudentGrade, etUserStudentNewPassword, etUserStudentNewPasswordConfirm, etUserStudentCity;
    private Button btnUpdateProfile;
    String selectedUid;
    UserStudent selectedUser;

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

        UserStudent currentUser = (UserStudent) SharedPreferencesUtils.getUser(this);
        assert currentUser != null;
        selectedUid = currentUser.getId();

        Log.d(TAG, "Selected user: " + selectedUid);

        // Initialize the EditText fields
        etUserStudentFirstName = findViewById(R.id.firstNameStudentChange);
        etUserStudentLastName = findViewById(R.id.lastNameStudentChange);
        etUserStudentId = findViewById(R.id.idNumberStudentChange);
        etUserStudentPhone = findViewById(R.id.phoneStudentChange);
        etUserStudentPassword = findViewById(R.id.nowPasswordStudentChange);
        etUserStudentNewPassword = findViewById(R.id.newPasswordStudent);
        etUserStudentNewPasswordConfirm = findViewById(R.id.newPasswordStudentConfirm);
        etUserStudentCity = findViewById(R.id.cityStudentChange);
        etUserStudentSchool = findViewById(R.id.schoolNameChange);
        etUserStudentGrade = findViewById(R.id.gradeNameChange);
        btnUpdateProfile = findViewById(R.id.saveChangesStudentBtn);


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
        databaseService.getUserStudent(selectedUid, new DatabaseService.DatabaseCallback<UserStudent>() {
            @Override
            public void onCompleted(UserStudent user) {
                selectedUser = user;
                SharedPreferencesUtils.saveUser(ChangeDetailsStudent.this, user);
                // Set the user data to the EditText fields
                etUserStudentFirstName.setText(selectedUser.getFirstName());
                etUserStudentLastName.setText(selectedUser.getLastName());
                etUserStudentId.setText(selectedUser.getId());
                etUserStudentCity.setText(selectedUser.getCity());
                etUserStudentSchool.setText(selectedUser.getSchoolName());
                etUserStudentGrade.setText(selectedUser.getGradeLevel());
                etUserStudentPhone.setText(selectedUser.getPhoneNumber());
                etUserStudentPassword.setText(selectedUser.getPassword() + "");
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
        String firstName = etUserStudentFirstName.getText().toString();
        String lastName = etUserStudentLastName.getText().toString();
        String phone = etUserStudentPhone.getText().toString();
        String city = etUserStudentCity.getText().toString();
        String school = etUserStudentSchool.getText().toString();
        String classlevel = etUserStudentGrade.getText().toString();
        String password = etUserStudentNewPassword.getText().toString() + "";
        String confPass = etUserStudentNewPasswordConfirm.getText().toString() +"";

        if (!isValid(firstName, lastName, phone, password, confPass)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // Update the user object
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setPhoneNumber(phone);
        selectedUser.setCity(city);
        selectedUser.setSchoolName(school);
        selectedUser.setGradeLevel(classlevel);
        selectedUser.setPassword(password);

        // Update the user data in the authentication
        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "User password: " + selectedUser.getPassword());

        updateUserInDatabase(selectedUser);
    }
    private void updateUserInDatabase(UserStudent user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(ChangeDetailsStudent.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                showUserProfile(); // Refresh the profile view
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(ChangeDetailsStudent.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String firstName, String lastName, String phone, String password,String confirmPass) {
        if (!validator.isNameValid(firstName)) {
            etUserStudentFirstName.setError("First name is required");
            etUserStudentFirstName.requestFocus();
            return false;
        }
        if (!validator.isNameValid(lastName)) {
            etUserStudentLastName.setError("Last name is required");
            etUserStudentLastName.requestFocus();
            return false;
        }
        if (!validator.isPhoneValid(phone)) {
            etUserStudentPhone.setError("Phone number is required");
            etUserStudentPhone.requestFocus();
            return false;
        }
        if(!password.equals(confirmPass)) {
            etUserStudentNewPassword.setError("Passwords are not similar");
            etUserStudentNewPassword.requestFocus();
            return false;
        }

        return true;
    }
}