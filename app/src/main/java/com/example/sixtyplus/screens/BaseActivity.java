package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserGeneral;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DatabaseService databaseService;
    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    protected boolean hasSideMenu() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseService = DatabaseService.getInstance();

        super.setContentView(R.layout.activity_base);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.nav_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // עדכון שם המשתמש בheader
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.usernameText);
        UserGeneral user = SharedPreferencesUtils.getUser(this);
        if (user != null) {
            usernameText.setText("שלום, " + user.getFirstName() + " " + user.getLastName() + "!");
        }

        // טעינת המנו לפי סוג המשתמש
        boolean isInCharge = SharedPreferencesUtils.isUserInCharge(this);
        boolean isAdmin = SharedPreferencesUtils.isAdmin(this);

        if (isInCharge) {
            navigationView.inflateMenu(R.menu.nav_menu_in_charge);
        } else {
            navigationView.inflateMenu(R.menu.nav_menu_student);
        }

        // הסתרת רשימות למי שאינו admin
        if (!isAdmin) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_students_list).setVisible(false);
            menu.findItem(R.id.nav_managers_list).setVisible(false);
        }

        if (hasSideMenu()) {

            toggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.open_drawer,
                    R.string.close_drawer
            );
            drawerLayout.addDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setTitle("");
            }

        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentLayout(layoutResID);
    }

    protected void setContentLayout(int layoutResId) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResId, contentFrame, true);
    }

    protected void navigateTo(Class<?> targetActivity) {
        if (!this.getClass().equals(targetActivity)) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // ===== משותף לשניהם =====
        if (id == R.id.nav_signOut) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showLogoutDialog();
            return true;
        }

        if (id == R.id.nav_students_list) {
            navigateTo(UserStudentsList.class);
            return true;
        }

        if (id == R.id.nav_managers_list) {
            navigateTo(UserInChargesList.class);
            return true;
        }

        // ===== תלמיד =====
        if (id == R.id.nav_home_student) {
            navigateTo(MainActivityStudents.class);

        } else if (id == R.id.nav_search_volunteering) {
            navigateTo(FindPlaces.class);

        } else if (id == R.id.nav_add_volunteering) {
            navigateTo(RegisterVolunteering.class);

        } else if (id == R.id.nav_history) {
            navigateTo(HistoryVolunteers.class);

        } else if (id == R.id.nav_weekly_student) {
            ///navigateTo(WeeklyStudentActivity.class);

        } else if (id == R.id.nav_update_details_student) {
            navigateTo(ChangeDetailsStudent.class);

        } else if (id == R.id.nav_instructions_student) {
            ///navigateTo(InstructionsStudentActivity.class);

            // ===== אחראי =====
        } else if (id == R.id.nav_home_manager) {
            navigateTo(MainActivityInCharge.class);

        } else if (id == R.id.nav_volunteering_requests) {
            navigateTo(AcceptingVolunteers.class);

        } else if (id == R.id.nav_cancel_volunteering) {
            ///navigateTo(CancelVolunteeringActivity.class);

        } else if (id == R.id.nav_weekly_manager) {
            ///navigateTo(WeeklyManagerActivity.class);

        } else if (id == R.id.nav_update_details_manager) {
            navigateTo(ChangeDetailsInCharge.class);

        } else if (id == R.id.nav_instructions_manager) {
            ///navigateTo(InstructionsManagerActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        View titleView = getLayoutInflater().inflate(R.layout.item_dialog_title, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage("בטוח שברצונך להתנתק?")
                .setPositiveButton("כן", (d, which) -> {
                    SharedPreferencesUtils.signOutUser(this);
                    Intent intent = new Intent(this, Landing.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("לא", (d, which) -> d.dismiss())
                .show();


        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setTypeface(ResourcesCompat.getFont(this, R.font.rubiklight));
        }

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setTypeface(ResourcesCompat.getFont(this, R.font.gveretlevin));
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                .setTypeface(ResourcesCompat.getFont(this, R.font.gveretlevin));
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}