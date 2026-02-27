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

public class RegisterInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterInCharge";
    private EditText etPasswordInCharge, etFNameInCharge, etLNameInCharge, etPhoneInCharge,
            etAdressInCharge, etIdNumberInCharge, etPlaceName, etConfPass;
    private Spinner etCityInCharge;
    private Button btnRegisterInCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_in_charge);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_in_charge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPasswordInCharge = findViewById(R.id.passwordInCharge);
        etFNameInCharge = findViewById(R.id.firstNameInCharge);
        etLNameInCharge = findViewById(R.id.lastNameInCharge);
        etPhoneInCharge = findViewById(R.id.phoneInCharge);
        etCityInCharge = findViewById(R.id.cityInCharge);
        etPlaceName = findViewById(R.id.placeName);
        etAdressInCharge = findViewById(R.id.placeAdress);
        etIdNumberInCharge = findViewById(R.id.idNumberInCharge);
        etConfPass = findViewById(R.id.confirmPasswordInCharge);
        btnRegisterInCharge = findViewById(R.id.registerBtn);

        String[] regions = getResources().getStringArray(R.array.regions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_selected, android.R.id.text1, regions);
        adapter.setDropDownViewResource(R.layout.spinner_item_selected);
        etCityInCharge.setAdapter(adapter);

        btnRegisterInCharge.setOnClickListener(this);

        findViewById(R.id.moveToLoginInCharge).setOnClickListener(view -> {
            startActivity(new Intent(RegisterInCharge.this, Login.class));
        });

        findViewById(R.id.goBackInCharge).setOnClickListener(view -> {
            startActivity(new Intent(RegisterInCharge.this, Landing.class));
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegisterInCharge.getId()) {
            String password = etPasswordInCharge.getText().toString().trim();
            String fName = etFNameInCharge.getText().toString().trim();
            String lName = etLNameInCharge.getText().toString().trim();
            String phone = etPhoneInCharge.getText().toString().trim();
            String city = etCityInCharge.getSelectedItem().toString();
            int cityPosition = etCityInCharge.getSelectedItemPosition();
            String adress = etAdressInCharge.getText().toString().trim();
            String place = etPlaceName.getText().toString().trim();
            String idNumber = etIdNumberInCharge.getText().toString().trim();
            String confPass = etConfPass.getText().toString().trim();

            if (!checkInput(password, confPass, fName, lName, phone, idNumber, cityPosition)) {
                return;
            }

            registerUser(password, fName, lName, phone, city, place, adress, new ArrayList<>(), null, idNumber);
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
            Toast.makeText(this, "על תעודת הזהות להיות בעלת 9 ספרות", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String password, String fName, String lName, String phone, String city, String place,
                              String adress, List<DayAndHours> schedule, String desc, String idNumber) {

        UserInCharge user = new UserInCharge(UserInCharge.class.getName(), idNumber, fName, lName,
                phone, city, password, place, adress, schedule, desc, false);

        databaseService.checkIfIdExists(idNumber, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean idExists) {
                if (idExists) {
                    Toast.makeText(RegisterInCharge.this, "תעודת הזהות כבר קיימת במערכת!", Toast.LENGTH_LONG).show();
                } else {
                    databaseService.checkIfPhoneExists(phone, new DatabaseService.DatabaseCallback<Boolean>() {
                        @Override
                        public void onCompleted(Boolean phoneExists) {
                            if (phoneExists) {
                                Toast.makeText(RegisterInCharge.this, "מספר הטלפון כבר רשום במערכת!", Toast.LENGTH_LONG).show();
                            } else {
                                createUserInDatabase(user);
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(RegisterInCharge.this, "שגיאה בבדיקת מספר טלפון", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterInCharge.this, "שגיאה בחיבור לנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInDatabase(UserInCharge user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                SharedPreferencesUtils.saveUser(RegisterInCharge.this, user);
                Intent mainIntent = new Intent(RegisterInCharge.this, Waiting.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterInCharge.this, "הרישום נכשל", Toast.LENGTH_SHORT).show();
                SharedPreferencesUtils.signOutUser(RegisterInCharge.this);
            }
        });
    }
}