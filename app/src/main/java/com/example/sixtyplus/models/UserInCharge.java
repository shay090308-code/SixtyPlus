package com.example.sixtyplus.models;

public class UserInCharge extends UserGeneral { //Those who are in charge of places.
    public String placeName; //Name of the place.
    public String adress; //Adress of the place.
    public String daysAvailable; //Days available to volunteer in this place.
    public String hrsAvailable; //Hours available to volunteer in this place.
    public boolean isAccepted; //

    public UserInCharge() {
    }

    public UserInCharge(String className, String idNumber, String firstName, String lastName,
                        String phoneNumber, String city, String password, String placeName,
                        String adress, String daysAvailable, String hrsAvailable, boolean isAccepted) {
        super(className, idNumber, firstName, lastName, phoneNumber, city, password);
        this.placeName = placeName;
        this.adress = adress;
        this.daysAvailable = daysAvailable;
        this.hrsAvailable = hrsAvailable;
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

    public String getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(String daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public String getHrsAvailable() {
        return hrsAvailable;
    }

    public void setHrsAvailable(String hrsAvailable) {
        this.hrsAvailable = hrsAvailable;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}