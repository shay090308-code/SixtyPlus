package com.example.sixtyplus.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class UserInCharge extends UserGeneral { //Those who are in charge of places.
    public String placeName; //Name of the place.
    public String adress; //Adress of the place.
    public List<DayAndHours> schedule;
    public String desc;
    public boolean accepted; //

    public UserInCharge() {
        this.schedule = new ArrayList<>();
    }

    public UserInCharge(String className, String idNumber, String firstName, String lastName,
                        String phoneNumber, String city, String password, String placeName,
                        String adress, List<DayAndHours> schedule, String desc, boolean accepted) {
        super(className, idNumber, firstName, lastName, phoneNumber, city, password);
        this.placeName = placeName;
        this.adress = adress;
        this.schedule = schedule;
        this.desc = desc;
        this.accepted = accepted;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public List<DayAndHours> getSchedule() {
        if (schedule == null) {
            schedule = new ArrayList<>();
        }
        return schedule;
    }

    public void setSchedule(List<DayAndHours> schedule) {
        this.schedule = schedule;
    }

    @Nullable
    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }


    @Exclude
    public DayAndHours getDayAndHours(Weekday weekday) {
        for (DayAndHours dayAndHours : this.getSchedule()) {
            if (dayAndHours.day.equals(weekday)) {
                return dayAndHours;
            }
        }
        DayAndHours dayAndHours = new DayAndHours(weekday, new HourMinute(0, 0), new HourMinute(0, 0));
        this.setDayAndHours(dayAndHours);
        return dayAndHours;
    }

    public void setDayAndHours(DayAndHours dayAndHours) {
        if (this.schedule == null) {
            this.schedule = new ArrayList<>();
        }
        for (DayAndHours dah : this.schedule) {
            if (dah.day.equals(dayAndHours.day)) {
                this.schedule.remove(dah);
                break;
            }
        }
        this.schedule.add(dayAndHours);
    }

}