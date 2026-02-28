package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserGeneral;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;

public class Login extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected boolean hasSideMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferencesUtils.signOutUser(Login.this);

        etPhone = findViewById(R.id.phonelogin);
        etPassword = findViewById(R.id.passwordlogin);
        btnLogin = findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(this);

        Button btnRegisterInCharge = findViewById(R.id.moveToRegister);
        btnRegisterInCharge.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SelectRegister.class);
            startActivity(intent);
        });

        Button forgotPass = findViewById(R.id.forgotPassword);
        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();

            if (!checkInput(phone, password)) return;

            loginUser(phone, password);
        }
    }

    private boolean checkInput(String phone, String password) {
        if (!validator.isPhoneValid(phone)) {
            etPhone.setError("Invalid phone number");
            etPhone.requestFocus();
            return false;
        }
        if (!validator.isPasswordValid(password)) {
            etPassword.setError("Password must be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginUser(String phone, String password) {
        databaseService.getUserByPhoneAndPassword(phone, password, new DatabaseService.DatabaseCallback<UserGeneral>() {
            @Override
            public void onCompleted(UserGeneral user) {
                if (user == null) {
                    etPassword.setError("Invalid or password");
                    etPassword.requestFocus();
                    return;
                }
                if (user.isUserInCharge()) {
                    loginUserInCharge(user);
                    return;
                }
                if (user.isUserStudent()) {
                    loginUserStudent(user);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                etPassword.setError("Invalid or password");
                etPassword.requestFocus();
                SharedPreferencesUtils.signOutUser(Login.this);
            }
        });
    }

    private void loginUserStudent(UserGeneral user) {
        databaseService.getUserStudent(user.id, new DatabaseService.DatabaseCallback<UserStudent>() {
            @Override
            public void onCompleted(UserStudent userStudent) {
                SharedPreferencesUtils.saveUser(Login.this, userStudent);
                Intent mainIntent = new Intent(Login.this, MainActivityStudents.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                etPassword.setError("Invalid or password");
                etPassword.requestFocus();
                SharedPreferencesUtils.signOutUser(Login.this);
            }
        });
    }

    private void loginUserInCharge(UserGeneral user) {
        databaseService.getUserInCharge(user.id, new DatabaseService.DatabaseCallback<UserInCharge>() {
            @Override
            public void onCompleted(UserInCharge userInCharge) {
                SharedPreferencesUtils.saveUser(Login.this, userInCharge);
                if (!userInCharge.isAccepted()) {
                    Intent waitingIntent = new Intent(Login.this, Waiting.class);
                    waitingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(waitingIntent);
                } else {
                    Intent intent = new Intent(Login.this, MainActivityInCharge.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                etPassword.setError("פרטים לא נכונים");
                etPassword.requestFocus();
                SharedPreferencesUtils.signOutUser(Login.this);
            }
        });
    }
}