package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.adapters.UserInChargeFindPlaces;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPlaces extends BaseActivity {

    private AutoCompleteTextView actvSearchPlaces;
    private UserInChargeFindPlaces adapter;
    private static final String TAG = "FindPlacesActivity";

    private List<UserInCharge> allPlaces;
    private Map<String, UserInCharge> placesMap;

    private UserStudent currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_places);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        actvSearchPlaces = findViewById(R.id.actvSearchPlaces);
        RecyclerView rvResults = findViewById(R.id.rvPlacesResults);

        allPlaces = new ArrayList<>();
        placesMap = new HashMap<>();

        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);
        if (currentStudent == null) {
            Toast.makeText(this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new UserInChargeFindPlaces(new UserInChargeFindPlaces.OnUserClickListener() {
            @Override
            public void onUserClick(UserInCharge user) {
                Intent intent = new Intent(FindPlaces.this, RegisterVolunteering.class);
                intent.putExtra("selectedPlaceId", user.getId());
                intent.putExtra("selectedPlaceName", user.getPlaceName());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(UserInCharge user) {}
        });

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        loadAllPlaces();
    }

    private void loadAllPlaces() {
        String studentCity = currentStudent.getCity();

        databaseService.getUserInChargeList(new DatabaseService.DatabaseCallback<List<UserInCharge>>() {
            @Override
            public void onCompleted(List<UserInCharge> users) {
                allPlaces.clear();
                placesMap.clear();

                for (UserInCharge user : users) {
                    if (user.isAccepted() &&
                            user.className != null &&
                            user.className.equals(UserInCharge.class.getName()) &&
                            user.getCity() != null &&
                            user.getCity().equals(studentCity)) {
                        allPlaces.add(user);
                        placesMap.put(user.getPlaceName(), user);
                    }
                }

                adapter.setUserList(allPlaces);
                setupAutoComplete();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(FindPlaces.this, "שגיאה בטעינת מקומות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutoComplete() {
        actvSearchPlaces.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();

                if (searchText.isEmpty()) {
                    adapter.setUserList(allPlaces);
                } else {
                    List<UserInCharge> filteredPlaces = new ArrayList<>();
                    for (UserInCharge place : allPlaces) {
                        if (place.getPlaceName() != null &&
                                place.getPlaceName().toLowerCase().contains(searchText)) {
                            filteredPlaces.add(place);
                        }
                    }
                    adapter.setUserList(filteredPlaces);
                }
            }
        });
    }

    abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }
}