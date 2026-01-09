package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

public class Waiting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String userId = SharedPreferencesUtils.getUser(this).id;
        DatabaseService.getInstance().getUserInCharge(userId, new DatabaseService.DatabaseCallback<UserInCharge>() {
            @Override
            public void onCompleted(UserInCharge user) {
                SharedPreferencesUtils.saveUser(Waiting.this, user);
                if (user.isAccepted()) {
                    Intent intent = new Intent(Waiting.this, MainActivityInCharge.class);
                    startActivity(intent);
                }


            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}