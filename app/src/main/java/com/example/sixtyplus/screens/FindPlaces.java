package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.adapters.UserInChargeFindPlaces;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPlaces extends AppCompatActivity {

    private AutoCompleteTextView actvSearchPlaces;
    private UserInChargeFindPlaces adapter;
    private DatabaseReference dbRef;
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

        // טעינת פרטי התלמיד המחובר
        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);
        if (currentStudent == null) {
            Toast.makeText(this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance("https://sixtyplus-bada2-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users");

        adapter = new UserInChargeFindPlaces(new UserInChargeFindPlaces.OnUserClickListener() {
            @Override
            public void onUserClick(UserInCharge user) {
                // מעבר למסך קביעת התנדבות עם המקום שנבחר
                Intent intent = new Intent(FindPlaces.this, RegisterVolunteering.class);
                intent.putExtra("selectedPlaceId", user.getId());
                intent.putExtra("selectedPlaceName", user.getPlaceName());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(UserInCharge user) {
            }
        });

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        // טעינת כל המקומות והגדרת ה-AutoComplete
        loadAllPlaces();
    }

    private void loadAllPlaces() {
        Log.d(TAG, "loadAllPlaces called");

        String studentCity = currentStudent.getCity();
        Log.d(TAG, "Student city: " + studentCity);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Total children in Firebase: " + snapshot.getChildrenCount());
                allPlaces.clear();
                placesMap.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    UserInCharge user = data.getValue(UserInCharge.class);
                    Log.d(TAG, "User found: " + (user != null ? "yes" : "null"));

                    if (user != null) {
                        Log.d(TAG, "User className: " + user.className);
                        Log.d(TAG, "User accepted: " + user.isAccepted());
                        Log.d(TAG, "User placeName: " + user.getPlaceName());
                        Log.d(TAG, "User city: " + user.getCity());

                        // סינון לפי: מאושר, מסוג UserInCharge, ובאותה עיר של התלמיד
                        if (user.isAccepted() &&
                                user.className != null &&
                                user.className.equals(UserInCharge.class.getName()) &&
                                user.getCity() != null &&
                                user.getCity().equals(studentCity)) {

                            allPlaces.add(user);
                            placesMap.put(user.getPlaceName(), user);
                            Log.d(TAG, "Added to results: " + user.getPlaceName());
                        }
                    }
                }

                Log.d(TAG, "Total results: " + allPlaces.size());

                // הצגת כל המקומות ב-RecyclerView
                adapter.setUserList(allPlaces);

                // הגדרת ה-AutoComplete
                setupAutoComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load places: " + error.getMessage());
                Toast.makeText(FindPlaces.this, "שגיאה בטעינת מקומות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutoComplete() {
        // סינון תוך כדי הקלדה - ללא dropdown
        actvSearchPlaces.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();

                if (searchText.isEmpty()) {
                    // אם השדה ריק, הצג את כל המקומות
                    adapter.setUserList(allPlaces);
                } else {
                    // סנן את המקומות לפי הטקסט
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

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // ביטול ה-dropdown
        actvSearchPlaces.setThreshold(Integer.MAX_VALUE);
    }
}