package com.example.sixtyplus.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.DayAndHours;
import com.example.sixtyplus.models.HourMinute;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.Weekday;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.ImageUtil;
import com.example.sixtyplus.utils.SharedPreferencesUtils;
import com.example.sixtyplus.utils.validator;
import com.example.sixtyplus.views.IntervalTimePicker;

import java.util.ArrayList;
import java.util.List;

public class ChangeDetailsInCharge extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";
    private static final int MAX_IMAGES = 6;
    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText etUserInChargefirstName, etUserInChargeLastName, etUserInChargeId,
            etUserInChargePhone, etUserInChargePassword, etUserInChargePlaceName,
            etUserInChargeAdress, etUserInChargeNewPassword,
            etUserInChargeDescription, etUserInChargeNewPasswordConfirm;
    private Button btnUpdateProfile, btnAddImage;
    private GridLayout imagesGrid;
    private String selectedUid;
    private Spinner etUserInChargeCity;
    private UserInCharge selectedUser;
    private String[] regions;
    private EditText startSun, startMon, startTue, startWed, startThu, startFri, startSat,
            endSun, endMon, endTue, endWed, endThu, endFri, endSat;
    private EditText[] startDays;
    private EditText[] endDays;
    private ArrayAdapter<String> adapter;
    private List<String> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_details_in_charge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_details_in_charge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageUtil.requestPermission(this);

        UserInCharge currentUser = (UserInCharge) SharedPreferencesUtils.getUser(this);
        assert currentUser != null;
        selectedUid = currentUser.getId();

        etUserInChargefirstName = findViewById(R.id.firstNameInChargeChange);
        etUserInChargeLastName = findViewById(R.id.lastNameInChargeChange);
        etUserInChargeId = findViewById(R.id.idNumberInChargeChange);
        etUserInChargePhone = findViewById(R.id.phoneInChargeChange);
        etUserInChargePassword = findViewById(R.id.nowPasswordInChargeChange);
        etUserInChargeNewPassword = findViewById(R.id.newPasswordInCharge);
        etUserInChargeNewPasswordConfirm = findViewById(R.id.newPasswordInChargeConfirm);
        etUserInChargeCity = findViewById(R.id.cityInChargeChange);
        etUserInChargePlaceName = findViewById(R.id.placeNameChange);
        etUserInChargeAdress = findViewById(R.id.placeAdressChange);
        etUserInChargeDescription = findViewById(R.id.placeDescription);
        btnUpdateProfile = findViewById(R.id.saveChangesInChargeBtn);
        imagesGrid = findViewById(R.id.imagesGrid);
        btnAddImage = findViewById(R.id.btnAddImage);

        regions = getResources().getStringArray(R.array.regions_array);
        adapter = new ArrayAdapter<>(this, R.layout.spinner_item_selected,
                android.R.id.text1, regions);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        etUserInChargeCity.setAdapter(adapter);

        startSun = findViewById(R.id.startSunday);
        startMon = findViewById(R.id.startMonday);
        startTue = findViewById(R.id.startTuesday);
        startWed = findViewById(R.id.startWednesday);
        startThu = findViewById(R.id.startThursday);
        startFri = findViewById(R.id.startFriday);
        startSat = findViewById(R.id.startSaturday);
        endSun = findViewById(R.id.endSunday);
        endMon = findViewById(R.id.endMonday);
        endTue = findViewById(R.id.endTuesday);
        endWed = findViewById(R.id.endWednesday);
        endThu = findViewById(R.id.endThursday);
        endFri = findViewById(R.id.endFriday);
        endSat = findViewById(R.id.endSaturday);

        startDays = new EditText[]{startSun, startMon, startTue, startWed, startThu, startFri, startSat};
        endDays = new EditText[]{endSun, endMon, endTue, endWed, endThu, endFri, endSat};

        for (EditText startDay : startDays) setIntervalTimePicker(startDay);
        for (EditText endDay : endDays) setIntervalTimePicker(endDay);

        btnUpdateProfile.setOnClickListener(this);
        btnAddImage.setOnClickListener(v -> openGallery());

        showUserProfile();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnUpdateProfile.getId()) {
            updateUserProfile();
        }
    }

    private void showUserProfile() {
        databaseService.getUserInCharge(selectedUid, new DatabaseService.DatabaseCallback<UserInCharge>() {
            @Override
            public void onCompleted(UserInCharge user) {
                selectedUser = user;
                SharedPreferencesUtils.saveUser(ChangeDetailsInCharge.this, user);

                etUserInChargefirstName.setText(selectedUser.getFirstName());
                etUserInChargeLastName.setText(selectedUser.getLastName());
                etUserInChargeId.setText(selectedUser.getId());

                if (selectedUser.getCity() != null) {
                    int spinnerPosition = adapter.getPosition(selectedUser.getCity());
                    if (spinnerPosition != -1) etUserInChargeCity.setSelection(spinnerPosition);
                }

                etUserInChargeAdress.setText(selectedUser.getAdress());
                etUserInChargePlaceName.setText(selectedUser.getPlaceName());
                etUserInChargePhone.setText(selectedUser.getPhoneNumber());
                etUserInChargePassword.setText(selectedUser.getPassword() + "");
                etUserInChargeDescription.setText(selectedUser.getDesc() + "");

                startSun.setText(selectedUser.getDayAndHours(Weekday.SUNDAY).getStartTime().toString());
                startMon.setText(selectedUser.getDayAndHours(Weekday.MONDAY).getStartTime().toString());
                startTue.setText(selectedUser.getDayAndHours(Weekday.TUESDAY).getStartTime().toString());
                startWed.setText(selectedUser.getDayAndHours(Weekday.WEDNESDAY).getStartTime().toString());
                startThu.setText(selectedUser.getDayAndHours(Weekday.THURSDAY).getStartTime().toString());
                startFri.setText(selectedUser.getDayAndHours(Weekday.FRIDAY).getStartTime().toString());
                startSat.setText(selectedUser.getDayAndHours(Weekday.SATURDAY).getStartTime().toString());
                endSun.setText(selectedUser.getDayAndHours(Weekday.SUNDAY).getEndTime().toString());
                endMon.setText(selectedUser.getDayAndHours(Weekday.MONDAY).getEndTime().toString());
                endTue.setText(selectedUser.getDayAndHours(Weekday.TUESDAY).getEndTime().toString());
                endWed.setText(selectedUser.getDayAndHours(Weekday.WEDNESDAY).getEndTime().toString());
                endThu.setText(selectedUser.getDayAndHours(Weekday.THURSDAY).getEndTime().toString());
                endFri.setText(selectedUser.getDayAndHours(Weekday.FRIDAY).getEndTime().toString());
                endSat.setText(selectedUser.getDayAndHours(Weekday.SATURDAY).getEndTime().toString());

                imagesList.clear();
                if (user.getImages() != null) {
                    imagesList.addAll(user.getImages());
                }
                runOnUiThread(() -> refreshImagesGrid());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        final String firstName = etUserInChargefirstName.getText().toString();
        final String lastName = etUserInChargeLastName.getText().toString();
        final String phone = etUserInChargePhone.getText().toString();
        final String city = etUserInChargeCity.getSelectedItem().toString();
        final String adress = etUserInChargeAdress.getText().toString();
        final String placeName = etUserInChargePlaceName.getText().toString();
        final String desc = etUserInChargeDescription.length() != 0
                ? etUserInChargeDescription.getText().toString() : " ";
        final String password = etUserInChargeNewPassword.getText().toString();
        final String confPass = etUserInChargeNewPasswordConfirm.getText().toString();

        if (!isValid(firstName, lastName, phone, password, confPass)) return;

        // בדיקת טלפון לפני העדכון כדי לוודא שאינו תפוס על ידי משתמש אחר
        databaseService.checkPhoneForUpdate(phone, selectedUser.getId(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean isTaken) {
                if (isTaken) {
                    Toast.makeText(ChangeDetailsInCharge.this, "מספר הטלפון כבר קיים במערכת", Toast.LENGTH_SHORT).show();
                } else {
                    for (Weekday weekday : Weekday.values()) {
                        String start = startDays[weekday.ordinal()].getText().toString();
                        String end = endDays[weekday.ordinal()].getText().toString();
                        if (!start.isEmpty() && !end.isEmpty()) {
                            selectedUser.setDayAndHours(new DayAndHours(weekday,
                                    HourMinute.fromString(start), HourMinute.fromString(end)));
                        }
                    }

                    selectedUser.setFirstName(firstName);
                    selectedUser.setLastName(lastName);
                    selectedUser.setPhoneNumber(phone);
                    selectedUser.setCity(city);
                    selectedUser.setAdress(adress);
                    selectedUser.setPlaceName(placeName);
                    selectedUser.setDesc(desc);
                    if (!password.isEmpty()) selectedUser.setPassword(password);
                    selectedUser.setImages(new ArrayList<>(imagesList));

                    updateUserInDatabase(selectedUser);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChangeDetailsInCharge.this, "שגיאה בבדיקת מספר הטלפון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInDatabase(UserInCharge user) {
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(ChangeDetailsInCharge.this,
                        "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                showUserProfile();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(ChangeDetailsInCharge.this,
                        "עדכון הפרטים נכשל", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String fName, String lName, String phone,
                            String password, String confirmPass) {
        if (password.isEmpty() && confirmPass.isEmpty()) return true;

        if (!validator.isPasswordValid(password)) {
            Toast.makeText(this, "על הסיסמא להיות בעלת 6 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isNameValid(fName)) {
            Toast.makeText(this, "על השם להיות בעל 2 תויים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isNameValid(lName)) {
            Toast.makeText(this, "על השם להיות בעל 2 תווים לפחות", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isPhoneValid(phone)) {
            Toast.makeText(this, "על מספר הטלפון להיות בעל 10 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validator.isConfirmPasswordValid(password, confirmPass)) {
            Toast.makeText(this, "הסיסמאות לא זהות", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openGallery() {
        if (imagesList.size() >= MAX_IMAGES) {
            Toast.makeText(this, "הגעת למקסימום של 6 תמונות", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            try {
                Bitmap bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(getContentResolver(), data.getData());

                ImageView tempView = new ImageView(this);
                tempView.setImageBitmap(bitmap);
                String base64 = ImageUtil.toBase64(tempView);

                if (base64 != null) {
                    imagesList.add(base64);
                    refreshImagesGrid();
                }
            } catch (Exception e) {
                Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshImagesGrid() {
        imagesGrid.removeAllViews();

        int sizeDp = 90;
        int sizePx = (int) (sizeDp * getResources().getDisplayMetrics().density);
        int marginPx = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < imagesList.size(); i++) {
            final int index = i;

            FrameLayout frame = new FrameLayout(this);
            GridLayout.LayoutParams frameParams = new GridLayout.LayoutParams();
            frameParams.width = sizePx;
            frameParams.height = sizePx;
            frameParams.setMargins(marginPx, marginPx, marginPx, marginPx);
            frame.setLayoutParams(frameParams);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setClipToOutline(true);
            android.graphics.drawable.GradientDrawable roundedBg =
                    new android.graphics.drawable.GradientDrawable();
            roundedBg.setCornerRadius(16f);
            roundedBg.setColor(android.graphics.Color.LTGRAY);
            imageView.setBackground(roundedBg);

            Bitmap bmp = ImageUtil.fromBase64(imagesList.get(i));
            imageView.setImageBitmap(bmp);

            ImageButton btnDelete = new ImageButton(this);
            FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(52, 52);
            deleteParams.gravity = Gravity.TOP | Gravity.END;
            btnDelete.setLayoutParams(deleteParams);
            btnDelete.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnDelete.setBackgroundColor(android.graphics.Color.parseColor("#CC000000"));
            btnDelete.setPadding(4, 4, 4, 4);
            btnDelete.setOnClickListener(v -> {
                imagesList.remove(index);
                refreshImagesGrid();
            });

            frame.addView(imageView);
            frame.addView(btnDelete);
            imagesGrid.addView(frame);
        }

        btnAddImage.setEnabled(imagesList.size() < MAX_IMAGES);
        btnAddImage.setAlpha(imagesList.size() < MAX_IMAGES ? 1f : 0.5f);
    }

    void setIntervalTimePicker(EditText timeEditText) {
        timeEditText.setFocusable(false);
        timeEditText.setClickable(true);
        timeEditText.setText("00:00");

        timeEditText.setOnClickListener(v -> {
            String currentTime = timeEditText.getText().toString();
            int currentHour = 0, currentMinute = 0;

            if (!currentTime.isEmpty() && currentTime.contains(":")) {
                String[] parts = currentTime.split(":");
                currentHour = Integer.parseInt(parts[0]);
                currentMinute = Integer.parseInt(parts[1]);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.time_picker_dialog, null);
            IntervalTimePicker timePicker = dialogView.findViewById(R.id.intervalTimePicker);
            timePicker.setHour(currentHour);
            timePicker.setMinute(currentMinute);

            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String time = String.format("%02d:%02d",
                                timePicker.getHour(), timePicker.getMinute());
                        timeEditText.setText(time);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}