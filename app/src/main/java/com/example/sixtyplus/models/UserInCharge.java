package com.example.sixtyplus.models;

import androidx.annotation.Nullable;

import java.util.List;

public class UserInCharge extends UserGeneral { //Those who are in charge of places.
    public String placeName; //Name of the place.
    public String adress; //Adress of the place.
    public List<DayAndHours> schedule;
    public String desc;
    public boolean isAccepted; //

    public UserInCharge() {
    }

    public UserInCharge(String className, String idNumber, String firstName, String lastName,
                        String phoneNumber, String city, String password, String placeName,
                        String adress, List<DayAndHours> schedule, String desc, boolean isAccepted) {
        super(className, idNumber, firstName, lastName, phoneNumber, city, password);
        this.placeName = placeName;
        this.adress = adress;
        this.schedule = schedule;
        this.desc = desc;
        this.isAccepted = isAccepted;
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
        return schedule;
    }

    public void setSchedule(List<DayAndHours> schedule) {
        this.schedule = schedule;
    }

    @Nullable
    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }
    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}