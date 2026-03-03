package com.example.sixtyplus.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserGeneral;
import com.example.sixtyplus.models.UserInCharge;
import com.example.sixtyplus.models.UserStudent;
import com.example.sixtyplus.services.DatabaseService;
import com.example.sixtyplus.utils.validator;

public class ForgotPassword extends BaseActivity {

    private static final String TAG = "forgotPwActivity";
    private EditText etUid, etUphone, etPw;
    private Button btnConfirm;

    @Override
    protected boolean hasSideMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUid = findViewById(R.id.idForgotPass);
        etUphone = findViewById(R.id.phoneForgotPass);
        etPw = findViewById(R.id.passwordForgotPass);
        btnConfirm = findViewById(R.id.SubmitBtnForgotPass);

        btnConfirm.setOnClickListener(v -> onConfirm());
    }

    private void onConfirm() {
        String idnum = etUid.getText().toString().trim();
        String phone = etUphone.getText().toString().trim();
        String pw = etPw.getText().toString().trim();

        if (!checkInput(idnum, phone, pw)) {
            Log.d(TAG, "Input Invalid, NOT ready to change password");
            return;
        }

        databaseService.findUserById(idnum, phone, new DatabaseService.DatabaseCallback<UserGeneral>() {
            @Override
            public void onCompleted(UserGeneral user) {
                if (user == null) {
                    Log.e(TAG, "Id does not exist: " + idnum);
                    etUid.setError("אין משתמש קיים עם תעודת הזהות הזו");
                    etUid.requestFocus();
                    return;
                }
                if (user.isUserStudent()) {
                    getUserStudentFromDB(idnum, pw);
                } else if (user.isUserInCharge()) {
                    getUserInChargeFromDB(idnum, pw);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Database error while searching for user", e);
                Toast.makeText(ForgotPassword.this, "שגיאה בגישה לשרת", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInChargeFromDB(String idnum, String pw) {
        databaseService.getUserStudent(idnum, new DatabaseService.DatabaseCallback<UserStudent>() {
            @Override
            public void onCompleted(UserStudent userStudent) {
                userStudent.setPassword(pw);
                updateUserInDB(userStudent, idnum);
            }

            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void getUserStudentFromDB(String idnum, String pw) {
        databaseService.getUserInCharge(idnum, new DatabaseService.DatabaseCallback<UserInCharge>() {
            @Override
            public void onCompleted(UserInCharge userInCharge) {
                userInCharge.setPassword(pw);
                updateUserInDB(userInCharge, idnum);
            }

            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void updateUserInDB(UserGeneral user, String idnum) {
        databaseService.writeUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Password updated successfully for: " + idnum);
                Toast.makeText(ForgotPassword.this, "הסיסמה עודכנה בהצלחה", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to update password", e);
                Toast.makeText(ForgotPassword.this, "שגיאה בעדכון הסיסמה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput(String username, String email, String pw) {
        if (!validator.checkidlength(username)) {
            etUid.setError("יש להזין תעודת זהות תקינה");
            etUid.requestFocus();
            return false;
        }
        if (!validator.isPhoneValid(email)) {
            etUphone.setError("יש להזין מספר טלפון תקין");
            etUphone.requestFocus();
            return false;
        }
        if (!validator.isPasswordValid(pw)) {
            etPw.setError("סיסמה חייבת להכיל לפחות 6 תווים");
            etPw.requestFocus();
            return false;
        }
        return true;
    }
}