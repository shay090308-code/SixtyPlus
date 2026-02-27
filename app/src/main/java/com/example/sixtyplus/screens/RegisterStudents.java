package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.Objects;

public class RegisterStudents extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterStudents";
    private EditText etPassword, etConfPass, etFName, etLName, etPhone, etSchoolName, etIdNumber, etGradeLevel;
    private Spinner etCityStudent;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_students);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_students), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPassword = findViewById(R.id.password);
        etFName = findViewById(R.id.firstName);
        etLName = findViewById(R.id.lastName);
        etPhone = findViewById(R.id.phone);
        etCityStudent = findViewById(R.id.cityStudent);
        etSchoolName = findViewById(R.id.schoolName);
        etGradeLevel = findViewById(R.id.gradeName);
        etIdNumber = findViewById(R.id.idNumber);
        etConfPass = findViewById(R.id.confirmPasswordStudent);
        btnRegister = findViewById(R.id.registerBtn);

        String[] regions = getResources().getStringArray(R.array.regions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_selected, android.R.id.text1, regions);
        adapter.setDropDownViewResource(R.layout.spinner_item_selected);
        etCityStudent.setAdapter(adapter);

        btnRegister.setOnClickListener(this);

        findViewById(R.id.moveToLogin).setOnClickListener(view -> {
            Intent intent = new Intent(RegisterStudents.this, Login.class);
            startActivity(intent);
        });

        findViewById(R.id.goBackStudent).setOnClickListener(view -> {
            Intent intent = new Intent(RegisterStudents.this, Landing.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            String passwordStudent = etPassword.getText().toString().trim();
            String fNameStudent = etFName.getText().toString().trim();
            String lNameStudent = etLName.getText().toString().trim();
            String phoneStudent = etPhone.getText().toString().trim();
            String cityStudent = etCityStudent.getSelectedItem().toString();
            int cityPosition = etCityStudent.getSelectedItemPosition();
            String schoolName = etSchoolName.getText().toString().trim();
            String gradeLevel = etGradeLevel.getText().toString().trim();
            String idnumber = etIdNumber.getText().toString().trim();
            String confirmpass = etConfPass.getText().toString().trim();

            if (!checkInput(passwordStudent, confirmpass, fNameStudent, lNameStudent, phoneStudent, idnumber, cityPosition)) {
                return;
            }

            registerUser(passwordStudent, fNameStudent, lNameStudent, phoneStudent, cityStudent, schoolName, gradeLevel, idnumber);
        }
    }

    private boolean checkInput(String password, String confPass, String fName, String lName, String phone, String id, int cityPosition) {
        if (cityPosition == 0) {
            Toast.makeText(this, "אנא בחר איזור מהרשימה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isPasswordValid(password)) {
            Toast.makeText(this, "על הסיסמא להיות בעלת 6 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isNameValid(fName) || !validator.isNameValid(lName)) {
            Toast.makeText(this, "על השם להיות בעל 2 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isPhoneValid(phone)) {
            Toast.makeText(this, "על מספר הטלפון להיות בעל 10 ספרות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isConfirmPasswordValid(password, confPass)) {
            Toast.makeText(this, "הסיסמאות לא זהות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.checkidlength(id)) {
            Toast.makeText(this, "על תעודת הזהות להיות בעלת 9 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String password, String fName, String lName, String phone, String city, String school, String grade, String idNumber) {
        UserStudent user = new UserStudent(UserStudent.class.getName(), idNumber, fName, lName, phone, city, password, school, grade);

        databaseService.checkIfIdExists(idNumber, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean idExists) {
                if (idExists) {
                    Toast.makeText(RegisterStudents.this, "תעודת זהות זו כבר רשומה במערכת!", Toast.LENGTH_LONG).show();
                } else {
                    databaseService.checkIfPhoneExists(phone, new DatabaseService.DatabaseCallback<Boolean>() {
                        @Override
                        public void onCompleted(Boolean phoneExists) {
                            if (phoneExists) {
                                Toast.makeText(RegisterStudents.this, "מספר הטלפון כבר קיים במערכת!", Toast.LENGTH_LONG).show();
                            } else {
                                createUserInDatabase(user);
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(RegisterStudents.this, "שגיאה בבדיקת טלפון", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterStudents.this, "שגיאה בחיבור לנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInDatabase(UserStudent user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                SharedPreferencesUtils.saveUser(RegisterStudents.this, user);
                Intent mainIntent = new Intent(RegisterStudents.this, MainActivityStudents.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterStudents.this, "הרישום נכשל, נסה שוב מאוחר יותר", Toast.LENGTH_SHORT).show();
                SharedPreferencesUtils.signOutUser(RegisterStudents.this);
            }
        });
    }
}