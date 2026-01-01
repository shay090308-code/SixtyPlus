package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

public class MainActivityInCharge extends AppCompatActivity {

    private static final String TAG = "LogOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_in_charge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnUpdateInChargeMain = findViewById(R.id.mainUpdateInCharge);
        btnUpdateInChargeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityInCharge.this, ChangeDetailsInCharge.class);
                startActivity(intent);
            }
        });

        Button btnUserStudentListMainInCharge = findViewById(R.id.mainUserStudentsListInCharge);
        btnUserStudentListMainInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityInCharge.this, UserStudentsList.class);
                startActivity(intent);
            }
        });

        Button btnUserInChargeListMainInCharge = findViewById(R.id.mainUserInChargeListInCharge);
        btnUserInChargeListMainInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityInCharge.this, UserInChargesList.class);
                startActivity(intent);
            }
        });

        Button btnLogOutMainInCharge = findViewById(R.id.mainLogOutInCharge);
        btnLogOutMainInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Sign out button clicked");
                SharedPreferencesUtils.signOutUser(MainActivityInCharge.this);

                Log.d(TAG, "User signed out, redirecting to LandingActivity");
                Intent landingIntent = new Intent(MainActivityInCharge.this, Landing.class);
                landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(landingIntent);
            }
        });
    }
}