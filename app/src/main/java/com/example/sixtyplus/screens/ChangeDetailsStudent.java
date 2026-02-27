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
import com.example.sixtyplus.models.UserGeneral;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

public class ChangeDetailsStudent extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserStudentFirstName, etUserStudentLastName, etUserStudentId, etUserStudentPhone, etUserStudentPassword,
            etUserStudentSchool, etUserStudentGrade, etUserStudentNewPassword, etUserStudentNewPasswordConfirm;
    private Button btnUpdateProfile;
    private String selectedUid;
    private String[] regions;

    private UserStudent selectedUser;
    private ArrayAdapter<String> adapter;
    private Spinner etUserStudentCity;

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

        selectedUid = getIntent().getStringExtra("USER_UID");
        if (selectedUid == null) {
            UserStudent currentUser = (UserStudent) SharedPreferencesUtils.getUser(this);
            assert currentUser != null;
            selectedUid = currentUser.getId();
        }

        Log.d(TAG, "Selected user: " + selectedUid);

        etUserStudentFirstName = findViewById(R.id.firstNameStudentChange);
        etUserStudentLastName = findViewById(R.id.lastNameStudentChange);
        etUserStudentId = findViewById(R.id.idNumberStudentChange);
        etUserStudentPhone = findViewById(R.id.phoneStudentChange);
        etUserStudentPassword = findViewById(R.id.nowPasswordStudentChange);
        etUserStudentNewPassword = findViewById(R.id.newPasswordStudent);
        etUserStudentNewPasswordConfirm = findViewById(R.id.newPasswordStudentConfirm);
        etUserStudentCity = findViewById(R.id.cityStudentChange);
        regions = getResources().getStringArray(R.array.regions_array);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_selected,
                android.R.id.text1,
                regions);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        etUserStudentCity.setAdapter(adapter);
        etUserStudentSchool = findViewById(R.id.schoolNameChange);
        etUserStudentGrade = findViewById(R.id.gradeNameChange);
        btnUpdateProfile = findViewById(R.id.saveChangesStudentBtn);

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
        databaseService.getUserStudent(selectedUid, new DatabaseService.DatabaseCallback<UserStudent>() {
            @Override
            public void onCompleted(UserStudent user) {
                selectedUser = user;
                UserGeneral loginUser = SharedPreferencesUtils.getUser(ChangeDetailsStudent.this);
                if (user.getId().equals(loginUser.getId()))
                    SharedPreferencesUtils.saveUser(ChangeDetailsStudent.this, user);

                etUserStudentFirstName.setText(selectedUser.getFirstName());
                etUserStudentLastName.setText(selectedUser.getLastName());
                etUserStudentId.setText(selectedUser.getId());
                if (selectedUser.getCity() != null) {
                    String userCity = selectedUser.getCity();
                    int spinnerPosition = adapter.getPosition(userCity);

                    if (spinnerPosition != -1) {
                        etUserStudentCity.setSelection(spinnerPosition);
                    }
                }
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

        final String firstName = etUserStudentFirstName.getText().toString();
        final String lastName = etUserStudentLastName.getText().toString();
        final String phone = etUserStudentPhone.getText().toString();
        final String city = etUserStudentCity.getSelectedItem().toString();
        final String school = etUserStudentSchool.getText().toString();
        final String classlevel = etUserStudentGrade.getText().toString();
        final String password = etUserStudentNewPassword.getText().toString() + "";
        final String confPass = etUserStudentNewPasswordConfirm.getText().toString() +"";

        if (!isValid(firstName, lastName, phone, password, confPass)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // בדיקת טלפון לפני העדכון כדי לוודא שאינו תפוס על ידי משתמש אחר
        databaseService.checkPhoneForUpdate(phone, selectedUser.getId(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean isTaken) {
                if (isTaken) {
                    Toast.makeText(ChangeDetailsStudent.this, "מספר הטלפון כבר קיים במערכת", Toast.LENGTH_SHORT).show();
                } else {
                    selectedUser.setFirstName(firstName);
                    selectedUser.setLastName(lastName);
                    selectedUser.setPhoneNumber(phone);
                    selectedUser.setCity(city);
                    selectedUser.setSchoolName(school);
                    selectedUser.setGradeLevel(classlevel);
                    if(!password.isEmpty())
                        selectedUser.setPassword(password);

                    updateUserInDatabase(selectedUser);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChangeDetailsStudent.this, "שגיאה בבדיקת מספר הטלפון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInDatabase(UserStudent user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(ChangeDetailsStudent.this, "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                showUserProfile();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(ChangeDetailsStudent.this, "עדכון הפרטים נכשל", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String fName, String lName, String phone, String password, String confirmPass) {

        if (!password.isEmpty() && !validator.isPasswordValid(password)) {
            Toast.makeText(this, "על הסיסמא להיות בעלת 6 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isNameValid(fName)) {
            Toast.makeText(this, "על השם להיות בעל 2 תויים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isNameValid(lName)) {
            Toast.makeText(this, "על השם להיות בעל 2 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.isPhoneValid(phone)) {
            Toast.makeText(this, "על מספר הטלפון להיות בעל 10 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!validator.isConfirmPasswordValid(password, confirmPass)) {
            Toast.makeText(this, "הסיסמאות לא זהות", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}