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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/// Activity for registering the people in charge of places
/// This activity is used to register the people in charge of places
/// It contains fields for the students to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterStudents";
    private EditText etPasswordInCharge, etFNameInCharge, etLNameInCharge, etPhoneInCharge, etCityInCharge,
            etAdressInCharge, etIdNumberInCharge, etPlaceName;
    private Button btnRegisterInCharge;

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
        /// set the layout for the activity
        setContentView(R.layout.activity_register_in_charge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_in_charge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        initViews();
        fillSpinners();

        /// get the views
        etPasswordInCharge = findViewById(R.id.passwordInCharge);
        etFNameInCharge = findViewById(R.id.firstNameInCharge);
        etLNameInCharge = findViewById(R.id.lastNameInCharge);
        etPhoneInCharge = findViewById(R.id.phoneInCharge);
        etCityInCharge = findViewById(R.id.cityInCharge);
        etPlaceName = findViewById(R.id.placeName);
        etAdressInCharge = findViewById(R.id.placeAdress);
        etIdNumberInCharge = findViewById(R.id.idNumberInCharge);

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


    // פונקציה כללית שיוצרת אובייקט ליום אחד
    public DayAndHours getDayData(String dayName, Spinner hStart, Spinner mStart, Spinner hEnd, Spinner mEnd) {

        DayAndHours dayObj = new DayAndHours();
        dayObj.day = dayName;

        // שליחת הטקסט שנבחר מהספינרים
        String selectedHStart = hStart.getSelectedItem().toString();
        String selectedMStart = mStart.getSelectedItem().toString();
        String selectedHEnd = hEnd.getSelectedItem().toString();
        String selectedMEnd = mEnd.getSelectedItem().toString();

        // הבדיקה החשובה שלך:
        // אם המשתמש השאיר את הכל על ברירת המחדל (לא בחר שעה)
        if (selectedHStart.equals("שעה") && selectedMStart.equals("דקה")) {
            dayObj.startTime = null;
            dayObj.endTime = null;
            dayObj.remark = "Closed this day";
        }
        else {
            // המשתמש בחר שעה - נחבר את השעות והדקות
            dayObj.startTime = selectedHStart + ":" + selectedMStart;
            dayObj.endTime = selectedHEnd + ":" + selectedMEnd;
            dayObj.remark = null; // נשאר null כי היום פתוח
        }

        return dayObj;
    }

    private void initViews() {
        // יום ראשון
        sunSH = findViewById(R.id.spinner_sun_start_hour);
        sunSM = findViewById(R.id.spinner_sun_start_min);
        sunEH = findViewById(R.id.spinner_sun_end_hour);
        sunEM = findViewById(R.id.spinner_sun_end_min);

        // יום שני
        monSH = findViewById(R.id.spinner_mon_start_hour);
        monSM = findViewById(R.id.spinner_mon_start_min);
        monEH = findViewById(R.id.spinner_mon_end_hour);
        monEM = findViewById(R.id.spinner_mon_end_min);

        // יום שלישי
        tueSH = findViewById(R.id.spinner_tue_start_hour);
        tueSM = findViewById(R.id.spinner_tue_start_min);
        tueEH = findViewById(R.id.spinner_tue_end_hour);
        tueEM = findViewById(R.id.spinner_tue_end_min);

        // יום רביעי
        wedSH = findViewById(R.id.spinner_wed_start_hour);
        wedSM = findViewById(R.id.spinner_wed_start_min);
        wedEH = findViewById(R.id.spinner_wed_end_hour);
        wedEM = findViewById(R.id.spinner_wed_end_min);

        // יום חמישי
        thuSH = findViewById(R.id.spinner_thu_start_hour);
        thuSM = findViewById(R.id.spinner_thu_start_min);
        thuEH = findViewById(R.id.spinner_thu_end_hour);
        thuEM = findViewById(R.id.spinner_thu_end_min);

        // יום שישי
        friSH = findViewById(R.id.spinner_fri_start_hour);
        friSM = findViewById(R.id.spinner_fri_start_min);
        friEH = findViewById(R.id.spinner_fri_end_hour);
        friEM = findViewById(R.id.spinner_fri_end_min);

        // יום שבת
        satSH = findViewById(R.id.spinner_sat_start_hour);
        satSM = findViewById(R.id.spinner_sat_start_min);
        satEH = findViewById(R.id.spinner_sat_end_hour);
        satEM = findViewById(R.id.spinner_sat_end_min);
    }

    private void fillSpinners() {
        // יצירת רשימת שעות (00-23)
        List<String> hours = new ArrayList<>();
        hours.add("שעה");
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        // יצירת רשימת דקות
        String[] mins = {"דקה", "00", "15", "30", "45"};

        ArrayAdapter<String> hAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(mins));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // הגדרת האדפטרים לכל 28 הספינרים
        Spinner[] hourSpinners = {sunSH, sunEH, monSH, monEH, tueSH, tueEH, wedSH, wedEH, thuSH, thuEH, friSH, friEH, satSH, satEH};
        Spinner[] minSpinners = {sunSM, sunEM, monSM, monEM, tueSM, tueEM, wedSM, wedEM, thuSM, thuEM, friSM, friEM, satSM, satEM};

        for (Spinner s : hourSpinners) {
            s.setAdapter(hAdapter);
        }
        for (Spinner s : minSpinners) {
            s.setAdapter(mAdapter);
        }
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
            if (!checkInput(passwordInCharge, fNameInCharge, lNameInCharge, phoneInCharge, idnumberInCharge)) {
                /// stop if input is invalid
                return;
            }
            List<DayAndHours> schedule = new ArrayList<>();
            schedule.add(getDayData("ראשון", sunSH, sunSM, sunEH, sunEM));
            schedule.add(getDayData("שני", monSH, monSM, monEH, monEM));
            schedule.add(getDayData("שלישי", tueSH, tueSM, tueEH, tueEM));
            schedule.add(getDayData("רביעי", wedSH, wedSM, wedEH, wedEM));
            schedule.add(getDayData("חמישי", thuSH, thuSM, thuEH, thuEM));
            schedule.add(getDayData("שישי", friSH, friSM, friEH, friEM));
            schedule.add(getDayData("שבת", satSH, satSM, satEH, satEM));

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(passwordInCharge, fNameInCharge, lNameInCharge,
                    phoneInCharge, cityInCharge, placeName, adressInCharge, schedule, null, idnumberInCharge);
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
                Intent mainIntent = new Intent(RegisterInCharge.this, MainActivityInCharge.class);
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