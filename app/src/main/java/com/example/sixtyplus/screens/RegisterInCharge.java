package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

import java.util.Objects;

/// Activity for registering the people in charge of places
/// This activity is used to register the people in charge of places
/// It contains fields for the students to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterStudents";
    private EditText etPasswordInCharge, etFNameInCharge, etLNameInCharge, etPhoneInCharge, etCityInCharge,
            etAdressInCharge, etIdNumberInCharge, etPlaceName, etDays, etHours, etDescription;
    private Button btnRegisterInCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_register_in_charge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_in_charge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        /// get the views
        etPasswordInCharge = findViewById(R.id.passwordInCharge);
        etFNameInCharge = findViewById(R.id.firstNameInCharge);
        etLNameInCharge = findViewById(R.id.lastNameInCharge);
        etPhoneInCharge = findViewById(R.id.phoneInCharge);
        etCityInCharge = findViewById(R.id.cityInCharge);
        etPlaceName = findViewById(R.id.placeName);
        etAdressInCharge = findViewById(R.id.placeAdress);
        etIdNumberInCharge = findViewById(R.id.idNumberInCharge);
        etHours = findViewById(R.id.placeHours);
        etDays = findViewById(R.id.placeDays);
        etDescription = findViewById(R.id.placeDescription);

        btnRegisterInCharge = findViewById(R.id.registerBtn);

        /// set the click listener
        btnRegisterInCharge.setOnClickListener(this);

        Button btnLoginInCharge = findViewById(R.id.moveToLoginInCharge);

        btnLoginInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegisterInCharge.this, Login.class);
                startActivity(intent);
            }
        });
        Button btnGoBackInCharge = findViewById(R.id.goBackInCharge);

        btnGoBackInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegisterInCharge.this, Landing.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegisterInCharge.getId()) {
            Log.d(TAG,"onClick: Register button clicked");

            /// get the input from the user
            String passwordInCharge = etPasswordInCharge.getText().toString();
            String fNameInCharge = etFNameInCharge.getText().toString();
            String lNameInCharge = etLNameInCharge.getText().toString();
            String phoneInCharge = etPhoneInCharge.getText().toString();
            String cityInCharge = etCityInCharge.getText().toString();
            String adressInCharge = etAdressInCharge.getText().toString();
            String placeName = etPlaceName.getText().toString();
            String idnumberInCharge = etIdNumberInCharge.getText().toString();
            String daysInCharge = etDays.getText().toString();
            String hoursInCharge = etHours.getText().toString();
            String descriptionPlace = etDescription.getText().toString();


            /// log the input
            Log.d(TAG, "onClick: Password: " + passwordInCharge);
            Log.d(TAG, "onClick: First Name: " + fNameInCharge);
            Log.d(TAG, "onClick: Last Name: " + lNameInCharge);
            Log.d(TAG, "onClick: Phone: " + phoneInCharge);
            Log.d(TAG, "onClick: City: " + cityInCharge);
            Log.d(TAG, "onClick: Adress: " + adressInCharge);
            Log.d(TAG, "onClick: Place Name: " + placeName);
            Log.d(TAG, "onClick: id: " + idnumberInCharge);
            Log.d(TAG, "onClick: Days: " + daysInCharge);
            Log.d(TAG, "onClick: Hours: " + hoursInCharge);
            Log.d(TAG, "onClick: Description: " + descriptionPlace);


            /// Validate input
            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(passwordInCharge, fNameInCharge, lNameInCharge, phoneInCharge, idnumberInCharge)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(passwordInCharge, fNameInCharge, lNameInCharge, phoneInCharge, cityInCharge,
                    placeName,adressInCharge, daysInCharge, hoursInCharge, descriptionPlace, idnumberInCharge);
        }
    }

    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    /// @see validator
    private boolean checkInput(String password, String fName, String lName, String phone, String id) {

        if (!validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            /// show error message to user
            etPasswordInCharge.setError("Password must be at least 6 characters long");
            /// set focus to password field
            etPasswordInCharge.requestFocus();
            return false;
        }

        if (!validator.isNameValid(fName)) {
            Log.e(TAG, "checkInput: First name must be at least 3 characters long");
            /// show error message to user
            etFNameInCharge.setError("First name must be at least 3 characters long");
            /// set focus to first name field
            etFNameInCharge.requestFocus();
            return false;
        }

        if (!validator.isNameValid(lName)) {
            Log.e(TAG, "checkInput: Last name must be at least 3 characters long");
            /// show error message to user
            etLNameInCharge.setError("Last name must be at least 3 characters long");
            /// set focus to last name field
            etLNameInCharge.requestFocus();
            return false;
        }

        if (!validator.isPhoneValid(phone)) {
            Log.e(TAG, "checkInput: Phone number must be at least 10 characters long");
            /// show error message to user
            etPhoneInCharge.setError("Phone number must be at least 10 characters long");
            /// set focus to phone field
            etPhoneInCharge.requestFocus();
            return false;
        }

        if (id == null || id.trim().isEmpty()) {
            etIdNumberInCharge.setError("id");
            etIdNumberInCharge.requestFocus();
            return false;
        }

        Log.d(TAG, "checkInput: Input is valid");
        return true;
    }

    /// Register the user
    private void registerUser(String password, String fName, String lName, String phone, String city, String place,
                              String adress, String days, String hours, String desc, String idNumber) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        UserInCharge user = new UserInCharge(UserInCharge.class.getName(), idNumber, fName, lName,
                                            phone, city, password, place, adress, days, hours, desc,false);

        databaseService.checkIfIdExists(user.getId(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exist) {
                if (exist) {
                    Toast.makeText(RegisterInCharge.this, "Id already exists!", Toast.LENGTH_LONG).show();
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

    private void createUserInDatabase(UserInCharge user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                SharedPreferencesUtils.saveUser(RegisterInCharge.this, user);
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterInCharge.this, LogOut.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterInCharge.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register
                SharedPreferencesUtils.signOutUser(RegisterInCharge.this);
            }
        });
    }
}