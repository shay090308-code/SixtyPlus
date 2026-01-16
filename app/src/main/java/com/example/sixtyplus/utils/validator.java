package com.example.sixtyplus.utils;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/// Validator class to validate user input.
/// This class contains static methods to validate user input,
/// like email, password, phone, name etc.
public class validator {


    /// Check if the password is valid
    /// @param password password to validate
    /// @return true if the password is valid, false otherwise
    public static boolean isPasswordValid(@Nullable String password) {
        return password != null && password.length() >= 6;
    }


    /// Check if the phone number is valid
    /// @param phone phone number to validate
    /// @return true if the phone number is valid, false otherwise
    /// @see Patterns#PHONE
    public static boolean isPhoneValid(@Nullable String phone) {
        return phone != null && phone.length() >= 10 && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isConfirmPasswordValid(@Nullable String password, @Nullable String confirmpassword) {
        return Objects.equals(password, confirmpassword);
    }

    /// Check if the name is valid
    /// @param name name to validate
    /// @return true if the name is valid, false otherwise
    public static boolean isNameValid(@Nullable String name) {
        return name != null && name.length() >= 2;
    }


    public static boolean checkidlength(String id) {
        return (id == null || id.trim().isEmpty() || id.length() == 9);
    }
}