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
import com.example.sixtyplus.adapters.Volunteeringhistoryadapter;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryVolunteers extends BaseActivity {

    private static final String TAG = "HistoryVolunteers";

    private RecyclerView rvVolunteeringHistory;
    private TextView tvNoHistory;
    private Volunteeringhistoryadapter adapter;

    private UserStudent currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_volunteers);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        initializeViews();
        loadVolunteeringHistory();
    }

    private void initializeViews() {
        rvVolunteeringHistory = findViewById(R.id.rvVolunteeringHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);

        currentStudent = (UserStudent) SharedPreferencesUtils.getUser(this);

        adapter = new Volunteeringhistoryadapter();

        // שינוי: הגדרת LayoutManager שמתחיל מלמעלה אבל מציג את הפריטים בסדר הפוך
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvVolunteeringHistory.setLayoutManager(layoutManager);
        rvVolunteeringHistory.setAdapter(adapter);
    }

    private void loadVolunteeringHistory() {
        if (currentStudent == null) {
            Toast.makeText(this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseService.getInstance().getVolunteeringList(new DatabaseService.DatabaseCallback<List<Volunteering>>() {
            @Override
            public void onCompleted(List<Volunteering> allVolunteering) {
                List<Volunteering> studentHistory = new ArrayList<>();

                // סינון רק ההתנדבויות של התלמיד הנוכחי
                for (Volunteering v : allVolunteering) {
                    if (v.getStudentId().equals(currentStudent.getId())) {
                        studentHistory.add(v);
                    }
                }

                if (studentHistory.isEmpty()) {
                    rvVolunteeringHistory.setVisibility(View.GONE);
                    tvNoHistory.setVisibility(View.VISIBLE);
                } else {
                    studentHistory.sort(new Comparator<Volunteering>() {
                        @Override
                        public int compare(Volunteering o1, Volunteering o2) {
                            return Long.compare(o1.dateMillis, o2.dateMillis);
                        }
                    });
                    Collections.reverse(studentHistory);

                    rvVolunteeringHistory.setVisibility(View.VISIBLE);
                    tvNoHistory.setVisibility(View.GONE);
                    adapter.setVolunteeringList(studentHistory);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load volunteering history", e);
                Toast.makeText(HistoryVolunteers.this, "שגיאה בטעינת היסטוריה", Toast.LENGTH_SHORT).show();
            }
        });
    }
}