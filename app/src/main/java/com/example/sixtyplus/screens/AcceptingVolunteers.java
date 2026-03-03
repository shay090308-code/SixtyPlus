package com.example.sixtyplus.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
                if (volunteering != null) {
                    volunteering.setStatus("approved");
                    updateVolunteeringStatus(volunteering, "ההתנדבות אושרה בהצלחה");
                }
            }

            @Override
            public void onReject(Volunteering volunteering) {
                if (volunteering != null) {
                    volunteering.setStatus("rejected");
                    updateVolunteeringStatus(volunteering, "ההתנדבות נדחתה");
                }
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

        Log.d(TAG, "loadPendingRequests: Loading requests for Place ID: " + currentUser.getId());

        databaseService.getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> allVolunteering) {
                List<Volunteering> pendingRequests = new ArrayList<>();

                if (allVolunteering != null) {
                    for (Volunteering v : allVolunteering) {
                        if (v != null && v.getPlaceId() != null && v.getStatus() != null) {

                            // השוואה בטוחה בין ה-ID של המקום ל-ID של המשתמש המחובר
                            boolean isMyPlace = v.getPlaceId().trim().equals(currentUser.getId().trim());
                            boolean isPending = v.getStatus().equalsIgnoreCase("pending");

                            if (isMyPlace && isPending) {
                                pendingRequests.add(v);
                            }
                        }
                    }
                }

                updateUI(pendingRequests);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Error fetching data", e);
                Toast.makeText(AcceptingVolunteers.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Volunteering> requests) {
        if (requests == null || requests.isEmpty()) {
            rvVolunteeringRequests.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.VISIBLE);
        } else {
            rvVolunteeringRequests.setVisibility(View.VISIBLE);
            tvNoRequests.setVisibility(View.GONE);
            adapter.setVolunteeringList(requests);
        }
    }

    private void updateVolunteeringStatus(Volunteering volunteering, String toastMessage) {
        databaseService.updateVolunteering(volunteering, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "updateVolunteeringStatus: Success");
                Toast.makeText(AcceptingVolunteers.this, toastMessage, Toast.LENGTH_SHORT).show();
                loadPendingRequests(); // טעינה מחדש של הרשימה לאחר העדכון
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "updateVolunteeringStatus: Failed", e);
                Toast.makeText(AcceptingVolunteers.this, "שגיאה בעדכון הסטטוס", Toast.LENGTH_SHORT).show();
            }
        });
    }
}