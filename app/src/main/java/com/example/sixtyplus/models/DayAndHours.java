package com.example.sixtyplus.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DayAndHours {
    public Weekday day;
    public HourMinute startTime;
    public HourMinute endTime;

    public DayAndHours() {
    }
    public DayAndHours(Weekday day, HourMinute startTime, HourMinute endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Weekday getDay() {
        return day;
    }

    public void setDay(Weekday day) {
        this.day = day;
    }

    public HourMinute getStartTime() {
        return startTime;
    }

    public void setStartTime(HourMinute startTime) {
        this.startTime = startTime;
    }

    public HourMinute getEndTime() {
        return endTime;
    }

    public void setEndTime(HourMinute endTime) {
        this.endTime = endTime;
    }

    @Exclude
    public boolean checkIfClosed() {
        return this.startTime.equals(this.endTime);
    }

}
