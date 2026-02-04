package com.example.sixtyplus.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserGeneral { //What is common for both kind of users.

    public String className;
    public String id;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String city;
    public String password;
    public boolean admin;

    public UserGeneral() {
    }

    public UserGeneral(String className, String id, String firstName, String lastName, String phoneNumber, String city, String password) {
        this.className = className;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.password = password;
        this.admin = false;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserGeneral{" +
                "className='" + className + '\'' +
                ", id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", city='" + city + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public boolean isUserInCharge() {
        return this.className.equals(UserInCharge.class.getName());
    }

    public boolean isUserStudent() {
        return this.className.equals(UserStudent.class.getName());
    }
}

