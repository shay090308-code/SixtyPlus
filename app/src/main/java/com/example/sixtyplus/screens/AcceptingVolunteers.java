package com.example.sixtyplus.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.adapters.Volunteeringrequestadapter;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class AcceptingVolunteers extends BaseActivity {

    private static final String TAG = "AcceptingVolunteers";

    private RecyclerView rvVolunteeringRequests;
    private TextView tvNoRequests;
    private Volunteeringrequestadapter adapter;

    private UserInCharge currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepting_volunteers);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        initializeViews();
        loadPendingRequests();
    }

    private void initializeViews() {
        rvVolunteeringRequests = findViewById(R.id.rvVolunteeringRequests);
        tvNoRequests = findViewById(R.id.tvNoRequests);

        currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);

        adapter = new Volunteeringrequestadapter(new Volunteeringrequestadapter.OnVolunteeringActionListener() {
            @Override
            public void onApprove(Volunteering volunteering) {
                approveVolunteering(volunteering);
            }

            @Override
            public void onReject(Volunteering volunteering) {
                rejectVolunteering(volunteering);
            }
        });

        rvVolunteeringRequests.setLayoutManager(new LinearLayoutManager(this));
        rvVolunteeringRequests.setAdapter(adapter);
    }

    private void loadPendingRequests() {
        if (currentUser == null) {
            Toast.makeText(this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseService.getInstance().getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> allVolunteering) {
                List<Volunteering> pendingRequests = new ArrayList<>();

                for (Volunteering v : allVolunteering) {
                    if (v.getPlaceId().equals(currentUser.getId()) &&
                            v.getStatus().equals("pending")) {
                        pendingRequests.add(v);
                    }
                }

                if (pendingRequests.isEmpty()) {
                    rvVolunteeringRequests.setVisibility(View.GONE);
                    tvNoRequests.setVisibility(View.VISIBLE);
                } else {
                    rvVolunteeringRequests.setVisibility(View.VISIBLE);
                    tvNoRequests.setVisibility(View.GONE);
                    adapter.setVolunteeringList(pendingRequests);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load volunteering requests", e);
                Toast.makeText(AcceptingVolunteers.this, "שגיאה בטעינת בקשות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveVolunteering(Volunteering volunteering) {
        volunteering.setStatus("approved");

        DatabaseService.getInstance().updateVolunteering(volunteering, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void unused) {
                Toast.makeText(AcceptingVolunteers.this, "ההתנדבות אושרה!", Toast.LENGTH_SHORT).show();
                loadPendingRequests(); // רענון הרשימה
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to approve volunteering", e);
                Toast.makeText(AcceptingVolunteers.this, "שגיאה באישור ההתנדבות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectVolunteering(Volunteering volunteering) {
        volunteering.setStatus("rejected");

        DatabaseService.getInstance().updateVolunteering(volunteering, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void unused) {
                Toast.makeText(AcceptingVolunteers.this, "ההתנדבות נדחתה", Toast.LENGTH_SHORT).show();
                loadPendingRequests(); // רענון הרשימה
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to reject volunteering", e);
                Toast.makeText(AcceptingVolunteers.this, "שגיאה בדחיית ההתנדבות", Toast.LENGTH_SHORT).show();
            }
        });
    }
}