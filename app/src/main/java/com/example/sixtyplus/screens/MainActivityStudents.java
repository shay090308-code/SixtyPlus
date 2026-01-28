package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

public class MainActivityStudents extends AppCompatActivity {

    private static final String TAG = "LogOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_students);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnUpdateStudentsMain = findViewById(R.id.mainUpdateStudents);
        btnUpdateStudentsMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityStudents.this, ChangeDetailsStudent.class);
                startActivity(intent);
            }
        });

        Button btnUserStudentListMainStudents = findViewById(R.id.mainUserStudentsListStudents);
        btnUserStudentListMainStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityStudents.this, UserStudentsList.class);
                startActivity(intent);
            }
        });

        Button btnUserInChargeListMainStudents = findViewById(R.id.mainUserInChargeListStudents);
        btnUserInChargeListMainStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivityStudents.this, UserInChargesList.class);
                startActivity(intent);
            }
        });

        Button btnUserStudentRegisterVolunteering = findViewById(R.id.mainUserStudentRegisterVolunteering);
        btnUserStudentRegisterVolunteering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityStudents.this, RegisterVolunteering.class);
                startActivity(intent);
            }
        });

        Button btnUserStudentsFindPlaces = findViewById(R.id.mainUserStudentsFindPlaces);
        btnUserStudentsFindPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityStudents.this, FindPlaces.class);
                startActivity(intent);
            }
        });


        Button btnLogOutMainStudents = findViewById(R.id.mainLogOutStudents);
        btnLogOutMainStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Sign out button clicked");
                SharedPreferencesUtils.signOutUser(MainActivityStudents.this);

                Log.d(TAG, "User signed out, redirecting to LandingActivity");
                Intent landingIntent = new Intent(MainActivityStudents.this, Landing.class);
                landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(landingIntent);
            }
        });
    }
}