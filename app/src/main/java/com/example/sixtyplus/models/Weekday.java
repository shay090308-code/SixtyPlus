package com.example.sixtyplus.models;

import androidx.annotation.NonNull;

public enum Weekday {
    // These calls use the constructor defined below
    SUNDAY("Sunday"),

    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday");

    private final String dayName;

    // Constructor: Must be private or package-private
    Weekday(String dayName) {
        this.dayName = dayName;
    }

    // Getter to retrieve the string name
    public String getDayName() {
        return dayName;
    }

    // Optional: Override toString() so you can use it directly in adapters
    @NonNull
    @Override
    public String toString() {
        return dayName;
    }
}