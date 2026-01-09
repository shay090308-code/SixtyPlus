package com.example.sixtyplus.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class HourMinute implements Comparable<HourMinute>{

    int hour;
    int minute;

    public HourMinute(){
        this.hour = 0;
        this.minute = 0;
    }

    public HourMinute(int hour, int minute) {
        if (hour < 0 || 24 <= hour) throw new RuntimeException("time is not valid, hour is not in range");
        if (minute < 0 || 60 <= minute) throw new RuntimeException("time is not valid, minute is not in range");
        this.hour = hour;
        this.minute = minute;
    }

    public HourMinute(String time) {
        this(parseHour(time), parseMinute(time));
    }

    private static int parseHour(String time) {
        int index = time.indexOf(":");
        if (index == -1)
            throw new RuntimeException("time is not valid, missing :");
        return Integer.parseInt(time.substring(0, index));
    }

    private static int parseMinute(String time) {
        int index = time.indexOf(":");
        if (index == -1)
            throw new RuntimeException("time is not valid, missing :");
        return Integer.parseInt(time.substring(index + 1));
    }

    public static HourMinute fromString(String time) {
        return new HourMinute(time);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }

    @Override
    public int compareTo(HourMinute other) {
        // -1 this < other
        // 0 this == other
        // 1 this > other
        if (Integer.compare(this.hour, other.hour) == 0) {
            return Integer.compare(this.minute, other.minute);
        }
        return Integer.compare(this.hour, other.hour);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HourMinute that = (HourMinute) o;
        return hour == that.hour && minute == that.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }
}
