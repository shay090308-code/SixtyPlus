package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

public class Landing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.loginlanding);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Landing.this, Login.class);
                startActivity(intent);
            }
        });

        Button button2 = findViewById(R.id.studentRegisterBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Landing.this, SelectRegister.class);
                startActivity(intent);
            }
        });

        if (SharedPreferencesUtils.isUserSign(Landing.this)) {
            Intent intent;

            if (SharedPreferencesUtils.isUserStudent(Landing.this)) {
                intent = new Intent(Landing.this, MainActivityStudents.class);
            } else {
                UserInCharge userInCharge = (UserInCharge) SharedPreferencesUtils.getUser(Landing.this);
                assert userInCharge != null;
                if (userInCharge.isAccepted()) {
                    intent = new Intent(Landing.this, MainActivityInCharge.class);
                } else {
                    intent = new Intent(Landing.this, Waiting.class);
                }

            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}