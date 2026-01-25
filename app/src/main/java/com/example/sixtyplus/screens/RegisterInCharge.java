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
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// Activity for registering the people in charge of places
/// This activity is used to register the people in charge of places
/// It contains fields for the students to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterStudents";
    private EditText etPasswordInCharge, etFNameInCharge, etLNameInCharge, etPhoneInCharge,
            etAdressInCharge, etIdNumberInCharge, etPlaceName, etConfPass;
    private Spinner etCityInCharge;
    private Button btnRegisterInCharge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_register_in_charge);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
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
        String[] regions = getResources().getStringArray(R.array.regions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item_selected,
                android.R.id.text1,
                regions
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_selected);
        etCityInCharge.setAdapter(adapter);
        etPlaceName = findViewById(R.id.placeName);
        etAdressInCharge = findViewById(R.id.placeAdress);
        etIdNumberInCharge = findViewById(R.id.idNumberInCharge);
        etConfPass = findViewById(R.id.confirmPasswordInCharge);
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
            String cityInCharge = etCityInCharge.getSelectedItem().toString();
            int cityPosition = etCityInCharge.getSelectedItemPosition();
            String adressInCharge = etAdressInCharge.getText().toString();
            String placeName = etPlaceName.getText().toString();
            String idnumberInCharge = etIdNumberInCharge.getText().toString();
            String confPass = etConfPass.getText().toString();


            /// log the input
            Log.d(TAG, "onClick: Password: " + passwordInCharge);
            Log.d(TAG, "onClick: First Name: " + fNameInCharge);
            Log.d(TAG, "onClick: Last Name: " + lNameInCharge);
            Log.d(TAG, "onClick: Phone: " + phoneInCharge);
            Log.d(TAG, "onClick: City: " + cityInCharge);
            Log.d(TAG, "onClick: Adress: " + adressInCharge);
            Log.d(TAG, "onClick: Place Name: " + placeName);
            Log.d(TAG, "onClick: id: " + idnumberInCharge);


            /// Validate input
            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(passwordInCharge, confPass, fNameInCharge, lNameInCharge, phoneInCharge, idnumberInCharge, cityPosition)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(passwordInCharge, fNameInCharge, lNameInCharge,
                    phoneInCharge, cityInCharge, placeName, adressInCharge, new ArrayList<>(), null, idnumberInCharge);
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
    private void registerUser(String password, String fName, String lName, String phone, String city, String place,
                              String adress, List<DayAndHours> schedule, String desc, String idNumber) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        UserInCharge user = new UserInCharge(UserInCharge.class.getName(), idNumber, fName, lName,
                                            phone, city, password, place, adress, schedule, desc, false);

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
                Intent mainIntent = new Intent(RegisterInCharge.this, Waiting.class);
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