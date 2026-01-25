package com.example.sixtyplus.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindPlaces extends AppCompatActivity {

    private SearchView searchView;
    private UserInChargeFindPlaces adapter;
    private DatabaseReference dbRef;
    private static final String TAG = "FindPlacesActivity";

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

        searchView = findViewById(R.id.searchPlaces);
        RecyclerView rvResults = findViewById(R.id.rvPlacesResults);
        List<UserInCharge> placesList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance("https://sixtyplus-bada2-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users");
        adapter = new UserInChargeFindPlaces(new UserInChargeFindPlaces.OnUserClickListener() {
            @Override
            public void onUserClick(UserInCharge user) {
                Toast.makeText(FindPlaces.this, "נבחר: " + user.getPlaceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongUserClick(UserInCharge user) {
            }
        });

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        // תיקון ה-hint של SearchView
        setupSearchViewHint();

        // הגדרת ה-SearchView
        setupSearch();

        // טעינת כל המקומות בהתחלה
        loadAllPlaces();
    }


    private void setupSearchViewHint() {
        // גישה ל-TextView הפנימי של SearchView
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(searchTextId);

        if (searchText != null) {
            searchText.setHint("חיפוש מקום");
            searchText.setTextDirection(View.TEXT_DIRECTION_RTL);
            searchText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // חיפוש תוך כדי הקלדה
                if (!newText.trim().isEmpty()) {
                    firebaseSearch(newText);
                } else {
                    loadAllPlaces(); // טעינת כל המקומות כשמוחקים את החיפוש
                }
                return true;
            }
        });
    }

    private void loadAllPlaces() {
        Log.d(TAG, "loadAllPlaces called");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Total children in Firebase: " + snapshot.getChildrenCount());
                List<UserInCharge> results = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    UserInCharge user = data.getValue(UserInCharge.class);
                    Log.d(TAG, "User found: " + (user != null ? "yes" : "null"));

                    if (user != null) {
                        Log.d(TAG, "User className: " + user.className);
                        Log.d(TAG, "User accepted: " + user.isAccepted());
                        Log.d(TAG, "User placeName: " + user.getPlaceName());

                        if (user.isAccepted()) {
                            // בדיקה שזה אובייקט UserInCharge
                            if (user.className != null && user.className.equals(UserInCharge.class.getName())) {
                                results.add(user);
                                Log.d(TAG, "Added to results: " + user.getPlaceName());
                            }
                        }
                    }
                }

                Log.d(TAG, "Total results: " + results.size());
                adapter.setUserList(results);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load places: " + error.getMessage());
                Toast.makeText(FindPlaces.this, "שגיאה בטעינת מקומות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseSearch(String searchText) {
        String queryText = searchText.trim().toLowerCase();

        // טעינת כל המשתמשים ביצירת סינון לוקלי
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserInCharge> results = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    UserInCharge user = data.getValue(UserInCharge.class);

                    if (user != null && user.isAccepted()) {
                        // בדיקה שזה אובייקט UserInCharge
                        if (user.className != null && user.className.equals(UserInCharge.class.getName())) {
                            // סינון לוקלי - בדיקה אם שם המקום מכיל את טקסט החיפוש
                            if (user.getPlaceName() != null &&
                                    user.getPlaceName().toLowerCase().contains(queryText)) {
                                results.add(user);
                            }
                        }
                    }
                }

                // עדכון האדפטר עם התוצאות
                adapter.setUserList(results);

                // הודעה אם אין תוצאות
                if (results.isEmpty()) {
                    Log.d(TAG, "לא נמצאו תוצאות עבור: " + searchText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Search failed: " + error.getMessage());
                Toast.makeText(FindPlaces.this, "שגיאה בחיפוש", Toast.LENGTH_SHORT).show();
            }
        });
    }
}