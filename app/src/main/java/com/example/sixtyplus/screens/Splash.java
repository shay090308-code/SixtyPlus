package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.sixtyplus.models.UserGeneral;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;


public class Splash  extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 שניות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToCorrectScreen();
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void navigateToCorrectScreen() {
        // בדוק אם המשתמש מחובר
        if (!SharedPreferencesUtils.isUserSign(this)) {
            Log.d(TAG, "User not signed in, navigating to LandingActivity");
            startActivity(new Intent(this, Landing.class));
            finish();
            return;
        }

        //  בודק את המשתמש ומביא לעמוד הנכון אחרי הספלאש
        if (SharedPreferencesUtils.isUserInCharge(this)) {
            // משתמש מסוג UserInCharge
            handleInChargeUser();
        }
        else if (SharedPreferencesUtils.isUserStudent(this)) {
            // משתמש מסוג UserStudent
            Log.d(TAG, "User is Student, navigating to MainActivityStudents");
            startActivity(new Intent(this, MainActivityStudents.class));
            finish();
        }
        else {
            Log.e(TAG, "Unknown user type, navigating to LandingActivity");
            startActivity(new Intent(this, Landing.class));
            finish();
        }
    }

    private void handleInChargeUser() {
        // קבל את המשתמש כ-UserInCharge
        UserInCharge userInCharge = (UserInCharge) SharedPreferencesUtils.getUser(this);

        if (userInCharge == null) {
            Log.e(TAG, "UserInCharge is null, navigating to LandingActivity");
            startActivity(new Intent(this, Landing.class));
            finish();
            return;
        }

        if (userInCharge.isAccepted()) {
            Log.d(TAG, "UserInCharge is accepted, navigating to MainActivityInCharge");
            startActivity(new Intent(this, MainActivityInCharge.class));
            finish();
        } else {
            Log.d(TAG, "UserInCharge is not accepted, navigating to WaitingActivity");
            startActivity(new Intent(this, Waiting.class));
            finish();
        }
    }
}

