package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.adapters.UserStudentAdapter;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;

import java.util.List;

public class UserStudentsList extends BaseActivity {

    private static final String TAG = "UsersStudentListActivity";
    private UserStudentAdapter userAdapter;
    private TextView tvUserCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_students_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView usersList = findViewById(R.id.rvstudentlist);
        tvUserCount = findViewById(R.id.tvuserstudentcount);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserStudentAdapter(new UserStudentAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(UserStudent user) {
                // Handle user click
                Log.d(TAG, "User clicked: " + user);
                Intent intent = new Intent(UserStudentsList.this, ChangeDetailsStudent.class);
                intent.putExtra("USER_UID", user.getId());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(UserStudent user) {
                // Handle long user click
                Log.d(TAG, "User long clicked: " + user);
            }
        });
        usersList.setAdapter(userAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getUserStudentList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<UserStudent> users) {
                userAdapter.setUserList(users);
                tvUserCount.setText("מספר תלמידים כולל: " + users.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }

}