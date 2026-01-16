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

/// Activity for registering the student
/// This activity is used to register the student
/// It contains fields for the students to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterStudents extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterStudents";
    private EditText etPassword, etConfPass, etFName, etLName, etPhone, etSchoolName, etIdNumber, etGradeLevel;
    Spinner etCityStudent;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_register_students);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_students), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        /// get the views
        etPassword = findViewById(R.id.password);
        etFName = findViewById(R.id.firstName);
        etLName = findViewById(R.id.lastName);
        etPhone = findViewById(R.id.phone);
        etCityStudent = findViewById(R.id.cityStudent);
        String[] regions = getResources().getStringArray(R.array.regions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item_selected,
                android.R.id.text1,
                regions
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_selected);
        etCityStudent.setAdapter(adapter);
        etSchoolName = findViewById(R.id.schoolName);
        etGradeLevel = findViewById(R.id.gradeName);
        etIdNumber = findViewById(R.id.idNumber);
        etConfPass = findViewById(R.id.confirmPasswordStudent);
        btnRegister = findViewById(R.id.registerBtn);

        /// set the click listener
        btnRegister.setOnClickListener(this);

        Button btnLoginInCharge = findViewById(R.id.moveToLogin);

        btnLoginInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegisterStudents.this, Login.class);
                startActivity(intent);
            }
        });
        Button btnGoBackInCharge = findViewById(R.id.goBackStudent);

        btnGoBackInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegisterStudents.this, Landing.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Log.d(TAG,"onClick: Register button clicked");

            /// get the input from the user
            String passwordStudent = etPassword.getText().toString();
            String fNameStudent = etFName.getText().toString();
            String lNameStudent = etLName.getText().toString();
            String phoneStudent = etPhone.getText().toString();
            String cityStudent = etCityStudent.getSelectedItem().toString();
            int cityPosition = etCityStudent.getSelectedItemPosition();
            String schoolName = etSchoolName.getText().toString();
            String gradeLevel = etGradeLevel.getText().toString();
            String idnumber = etIdNumber.getText().toString();
            String confirmpass = etConfPass.getText().toString();


            /// log the input
            Log.d(TAG, "onClick: Password: " + passwordStudent);
            Log.d(TAG, "onClick: First Name: " + fNameStudent);
            Log.d(TAG, "onClick: Last Name: " + lNameStudent);
            Log.d(TAG, "onClick: Phone: " + phoneStudent);
            Log.d(TAG, "onClick: City: " + cityStudent);
            Log.d(TAG, "onClick: Adress: " + schoolName);
            Log.d(TAG, "onClick: Place Name: " + gradeLevel);
            Log.d(TAG, "onClick: id: " + idnumber);

            /// Validate input
            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(passwordStudent, confirmpass, fNameStudent, lNameStudent, phoneStudent, idnumber, cityPosition)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(passwordStudent, fNameStudent, lNameStudent, phoneStudent, cityStudent,
                    schoolName,gradeLevel, idnumber);
        }
    }

    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    /// @see validator
    private boolean checkInput(String password, String confPass, String fName, String lName, String phone, String id, int cityPosition) {

        if (cityPosition == 0) {
            Toast.makeText(this, "אנא בחר איזור מהרשימה", Toast.LENGTH_SHORT).show();
            return false;
        }

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

        if(!validator.isConfirmPasswordValid(password, confPass)) {
            Log.e(TAG, "checkInput: Passwords do not match");
            Toast.makeText(this, "הסיסמאות לא זהות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.checkidlength(id)) {
            Toast.makeText(this, "על תעודת הזהות להיות בעלת 9 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG, "checkInput: Input is valid");
        return true;
    }


    /// Register the user
    private void registerUser(String password, String fName, String lName, String phone, String city, String school,
                              String grade, String idNumber) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        UserStudent user = new UserStudent(UserStudent.class.getName(), idNumber, fName, lName,
                phone, city, password, school, grade);

        databaseService.checkIfIdExists(user.getId(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exist) {
                if (exist) {
                    Toast.makeText(RegisterStudents.this, "Id already exists!", Toast.LENGTH_LONG).show();
                } else {
                    createUserInDatabase(user);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("TAG", Objects.requireNonNull(e.getMessage()));
            }
        });

    }

    private void createUserInDatabase(UserStudent user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                SharedPreferencesUtils.saveUser(RegisterStudents.this, user);
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterStudents.this, MainActivityStudents.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterStudents.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register
                SharedPreferencesUtils.signOutUser(RegisterStudents.this);
            }
        });
    }
}